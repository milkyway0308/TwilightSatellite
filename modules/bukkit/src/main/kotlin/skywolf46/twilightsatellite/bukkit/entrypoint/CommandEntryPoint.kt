package skywolf46.twilightsatellite.bukkit.entrypoint

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import io.github.classgraph.ClassInfo
import io.github.classgraph.ClassInfoList
import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import skywolf46.atmospherereentry.common.api.annotations.EntryPointContainer
import skywolf46.atmospherereentry.common.api.annotations.EntryPointWorker
import skywolf46.atmospherereentry.common.api.annotations.IgnoreException
import skywolf46.atmospherereentry.common.api.util.printError
import skywolf46.twilightsatellite.bukkit.TwilightSatellite
import skywolf46.twilightsatellite.bukkit.annotations.commands.*
import skywolf46.twilightsatellite.bukkit.commands.ArgumentConsumer
import skywolf46.twilightsatellite.bukkit.commands.ArgumentConverter
import skywolf46.twilightsatellite.bukkit.commands.CommandMatcher
import skywolf46.twilightsatellite.bukkit.commands.FilterableIterator
import skywolf46.twilightsatellite.bukkit.commands.exceptions.ConditionMismatchedException
import skywolf46.twilightsatellite.bukkit.commands.matcher.PseudoTypeWrappedMatcher
import skywolf46.twilightsatellite.bukkit.commands.matcher.RequiredPlaceHolderTypeMatcher
import skywolf46.twilightsatellite.bukkit.commands.matcher.StrictCommandMatcher
import skywolf46.twilightsatellite.bukkit.commands.storages.ArgumentConverterStorage
import skywolf46.twilightsatellite.bukkit.commands.storages.CommandMatcherStorage
import skywolf46.twilightsatellite.bukkit.data.BukkitCommandStorage
import skywolf46.twilightsatellite.bukkit.data.WrappedBukkitCommand
import skywolf46.twilightsatellite.bukkit.event.CommandSuggestionEvent

@EntryPointContainer(dependsOn = [BukkitEntryPoint::class])
@IgnoreException([ClassNotFoundException::class])
class CommandEntryPoint : KoinComponent {
    init {
        Class.forName("org.bukkit.Bukkit")
    }

    @EntryPointWorker
    fun onInit() {
        initComponents()
        val consumers = mutableListOf<ClassInfo>()
        val matchers = mutableListOf<ClassInfo>()
        val converters = mutableListOf<ClassInfo>()
        val commands = mutableListOf<ClassInfo>()
        get<ClassInfoList>().forEach {
            if (it.hasAnnotation(AutoArgConverter::class.java)) converters.add(it)
            if (it.hasAnnotation(AutoCommandMatcher::class.java)) matchers.add(it)
            if (it.hasAnnotation(AutoArgumentConsumer::class.java)) consumers.add(it)
            if (it.hasAnnotation(CommandContainer::class.java)) commands.add(it)
        }
        verifyConsumers(consumers).forEach {}

        verifyAutoMatchers(matchers).forEach {
            get<CommandMatcherStorage>().add(it)
            println("Registered command matcher ${it::class.simpleName}")
        }

        verifyAutoConverters(converters).forEach {

        }

        registerPseudoTypeTester()
        registerDefaultAutoCompleteProvider()
        registerAllCommands(commands)


        println("Baking commands...")
        get<BukkitCommandStorage>().bakeAll()
    }

    private fun initComponents() {
        loadKoinModules(module {
            single { ArgumentConverterStorage() }
            single { CommandMatcherStorage() }
            single { BukkitCommandStorage() }
        })
    }


    fun registerAllCommands(classes: List<ClassInfo>) {
        val bukkitStorage = get<BukkitCommandStorage>()
        val commandStorage = get<CommandMatcherStorage>()
        val literals = mutableSetOf<String>()
        classes.forEach { classInfo ->
            println("Registering command ${classInfo.name}")
            val targetClass = classInfo.loadClass()
            val instance = runCatching {
                val constructor = targetClass.constructors.find { it.parameterCount == 0 } ?: run {
                    System.err.println("Failed to register command ${classInfo.name} : No empty constructor found.")
                    return@forEach
                }
                return@runCatching constructor.newInstance()
            }.getOrElse {
                System.err.println("Failed to register command ${classInfo.name} : ${it.javaClass.name} (${it.message})")
                return@forEach
            }
            val container = targetClass.getAnnotation(CommandContainer::class.java)
            val owner = targetClass.getAnnotation(CommandOwner::class.java)?.owner ?: Any::class
            val methods = targetClass.methods

            for (x in methods) {
                val annotation = x.getAnnotation(Command::class.java) ?: continue
                runCatching {
                    val commandBase = container.commands.filter { it.isNotBlank() }.map {
                        if (it.startsWith("/")) it.substring(1) else it
                    }
                    commandBase.forEach {
                        if (" " in it) {
                            literals.add(it.substringBefore(" "))
                        } else {
                            literals.add(it)
                        }
                    }
                    val isOrphanCommand = commandBase.isEmpty()
                    val prefetchedIterators = commandBase.map { parentCommand ->
                        val commands = annotation.commands.filter { it.isNotBlank() }
                        if (isOrphanCommand) {
                            if (commands.isEmpty()) return@runCatching else commands.map {
                                commandStorage.parseAllMatched(
                                    FilterableIterator(it.toList())
                                )
                            }

                        } else {
                            if (commands.isEmpty()) listOf(commandStorage.parseAllMatched(FilterableIterator((parentCommand).toList())))
                            else commands.map {
                                commandStorage.parseAllMatched(
                                    FilterableIterator(
                                        (if (it.startsWith("/")) it.substring(1) else "$parentCommand $it").toList()
                                    )
                                )
                            }
                        }
                    }.flatten().toSet()
                    for (matched in prefetchedIterators) {
                        if (matched.isEmpty()) {
                            System.err.println("Failed to register command ${annotation.commands.joinToString(" ")} : No command matched.")
                            continue
                        }
                        val command = matched.orNull()!!
                        x.isAccessible = true
                        bukkitStorage.registerAll(owner, command.toTypedArray(), {
                            runCatching {
                                x.invoke(instance, it)
                            }.onFailure { error ->
                                if (error is ConditionMismatchedException) return@onFailure
                                System.err.println("Failed to execute command ${annotation.commands.joinToString(" ")} : ${error.javaClass.name} (${error.message})")
                                error.printStackTrace()
                            }
                        }, 0)
                    }

                }.onFailure { error ->
                    System.err.println("Failed to register command with exception ${error.javaClass.name} (${error.message})")
                }
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(TwilightSatellite.instance) {
            Bukkit.getConsoleSender().sendMessage("§eTwilightSatellite §7| §7Finalizing command registration...")
            for (literal in literals) {
                Bukkit.getCommandMap().getCommand(literal)?.apply {
                    permission = null
                }
            }
        }
    }

    fun registerDefaultAutoCompleteProvider() {
        val bukkitStorage = get<BukkitCommandStorage>()
        bukkitStorage.addBukkitAutoCompleteProvider(StrictCommandMatcher::class.java) { matcher, storage ->
            val baseCommand = WrappedBukkitCommand(LiteralArgumentBuilder.literal(matcher.matcher))
//            if (storage.hasCommandRunner()) {
//                baseCommand.executes {
//                    storage.execute(it)
//                }
//            }
            storage.getCommands().forEach {
                baseCommand.then(bukkitStorage.bakeBukkitAutoComplete(it, storage.getCommand(it)!!, baseCommand))
            }
            return@addBukkitAutoCompleteProvider baseCommand
        }

        bukkitStorage.addBukkitAutoCompleteProvider(RequiredPlaceHolderTypeMatcher::class.java) { matcher, storage ->
            val strings = matcher.parameter
            val current = WrappedBukkitCommand(RequiredArgumentBuilder.argument<Any, Any?>(
                matcher.getCommandName() ?: "parameter", when (matcher.type) {
                    "string" -> StringArgumentType.string()
                    "string.." -> StringArgumentType.greedyString()
                    "int" -> {
                        val param = parseRangedParameter(strings)
                        if (param == null) {
                            IntegerArgumentType.integer()
                        } else {
                            IntegerArgumentType.integer(param.first, param.last)
                        }
                    }
                    else -> StringArgumentType.word()
                } as ArgumentType<Any?>
            ).apply {
                if (matcher.requireCompleteEvent()) {
                    suggests { context, builder ->
                        CommandSuggestionEvent(context, matcher, storage.owner, builder, !Bukkit.isPrimaryThread()).apply {
                            Bukkit.getPluginManager().callEvent(this)
                        }
                        builder.buildFuture()
                    }
                }
            })
//            if (storage.hasCommandRunner()) {
//                current.executes {
//                    storage.execute(it)
//                }
//            }
            storage.getCommands().forEach {
                current.then(bukkitStorage.bakeBukkitAutoComplete(it, storage.getCommand(it)!!, current))
            }
            return@addBukkitAutoCompleteProvider current
        }
    }

    private fun parseRangedParameter(param: String?): IntRange? {
        if (param == null) return null
        val split = param.split("-")
        if (split.size > 2) {
            throw IllegalArgumentException("Invalid ranged parameter : $param")
        }
        if (split.size == 1) return split[0].toInt()..split[0].toInt()
        return split[0].toInt()..split[1].toInt()
    }

    private fun registerPseudoTypeTester() {
        PseudoTypeWrappedMatcher.registerTester("string") {
            it.addPreArgument(it.next())
            true
        }
        PseudoTypeWrappedMatcher.registerTester("string..") {
//            it.addPreArgument(it.nextAll())
            true
        }
        PseudoTypeWrappedMatcher.registerTester("int") {
            it.addPreArgument(it.next().toInt())
            true
        }
        PseudoTypeWrappedMatcher.registerTester("player") {
            val player = Bukkit.getPlayer(it.next()) ?: return@registerTester false
            it.addPreArgument(player)
            true
        }
    }

    private fun verifyConsumers(classes: List<ClassInfo>): List<ArgumentConsumer> {
        return classes.mapNotNull {
            verifyConsumer(it).getOrElse { error ->
                printError(error)
            } as? ArgumentConsumer
        }
    }

    private fun verifyAutoMatchers(classes: List<ClassInfo>): List<CommandMatcher> {
        return classes.mapNotNull {
            verifyAutoMatchers(it).getOrElse { error ->
                printError(error)
            } as? CommandMatcher
        }
    }

    private fun verifyAutoConverters(classes: List<ClassInfo>): List<ArgumentConverter<*, *>> {
        return classes.mapNotNull {
            verifyAutoConverters(it).getOrElse { error ->
                printError(error)
            } as? ArgumentConverter<*, *>
        }
    }

    private fun verifyConsumer(cls: ClassInfo): Either<String, ArgumentConsumer> {
        verifyConstructors(cls).onLeft {
            return it.left()
        }
        val instance = runCatching {
            val target = cls.loadClass()
            if (!ArgumentConsumer::class.java.isAssignableFrom(target)) return Either.Left("Failed to load class ${cls.name} : Class ${cls.name} is not instance of ArgumentConsumer.")
            target.newInstance() as ArgumentConsumer
        }.getOrElse {
            return Either.Left("Failed to load class ${cls.name} : ${it.message}")
        }
        return instance.right()
    }

    private fun verifyAutoMatchers(cls: ClassInfo): Either<String, CommandMatcher> {
        verifyConstructors(cls).onLeft {
            return it.left()
        }
        val instance = runCatching {
            val target = cls.loadClass()
            if (!CommandMatcher::class.java.isAssignableFrom(target)) return Either.Left("Failed to load class ${cls.name} : Class ${cls.name} is not instance of CommandMatcher.")
            target.newInstance() as CommandMatcher
        }.getOrElse {
            return Either.Left("Failed to load class ${cls.name} : ${it.javaClass.name}( ${it.message} )")
        }
        return instance.right()
    }

    private fun verifyAutoConverters(cls: ClassInfo): Either<String, ArgumentConverter<*, *>> {
        verifyConstructors(cls).onLeft {
            return it.left()
        }
        val instance = runCatching {
            val target = cls.loadClass()
            if (!ArgumentConverter::class.java.isAssignableFrom(target)) return Either.Left("Failed to load class ${cls.name} : Class ${cls.name} is not instance of ArgumentConverter.")
            target.newInstance() as ArgumentConverter<*, *>
        }.getOrElse {
            return Either.Left("Failed to load class ${cls.name} : ${it.message}")
        }
        return instance.right()
    }

    private fun verifyConstructors(cls: ClassInfo): Either<String, Unit> {
        if (cls.constructorInfo.isEmpty()) return Either.Left("Failed to load class ${cls.name} : Class ${cls.name} has no constructor.")
        if (!cls.constructorInfo.any { it.parameterInfo.isEmpty() }) return Either.Left("Failed to load class ${cls.name} : Class ${cls.name} requires empty constructor.")
        return Unit.right()
    }
}