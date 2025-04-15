package skywolf46.twilightsatellite.bukkit.data

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import skywolf46.atmospherereentry.common.api.annotations.IgnoreException
import skywolf46.twilightsatellite.bukkit.commands.CommandArgs
import skywolf46.twilightsatellite.bukkit.commands.CommandMatcher
import skywolf46.twilightsatellite.bukkit.commands.CommandStorage
import skywolf46.twilightsatellite.bukkit.commands.matcher.StrictCommandMatcher
import java.lang.reflect.Field
import kotlin.reflect.KClass

@IgnoreException([ClassNotFoundException::class])
class BukkitCommandStorage : CommandStorage<CommandSender>(Any::class) {
    init {
        addRestriction {
            if (it !is StrictCommandMatcher)
                throw IllegalArgumentException("First argument must be StrictCommandMatcher.")
        }
        if (Bukkit.getServer().name == "PaperPlane") {
            println("** PaperPlane bukkit modification detected. Using @ prefix for command.")
        }
    }

    private var requireBakeCommand = false

    private val commands = mutableMapOf<String, WrappedBukkitCommand>()

    private val dispatcher = extractDispatcher()

    private val autoCompleteProvider =
        mutableMapOf<Class<out CommandMatcher>, WrappedBukkitCommand.(CommandMatcher, CommandStorage<CommandSender>) -> WrappedBukkitCommand>()

    fun <T : CommandMatcher> addBukkitAutoCompleteProvider(
        target: Class<T>,
        unit: WrappedBukkitCommand.(T, CommandStorage<CommandSender>) -> WrappedBukkitCommand
    ) {
        autoCompleteProvider[target] =
            unit as WrappedBukkitCommand.(CommandMatcher, CommandStorage<CommandSender>) -> WrappedBukkitCommand
    }

    fun bakeBukkitAutoComplete(root: CommandMatcher): WrappedBukkitCommand {
        if (root !is StrictCommandMatcher)
            throw IllegalArgumentException("Root command must be StrictCommandMatcher.")
        val command = getCommand(root) ?: throw IllegalArgumentException("Command not found.")
        val wrappedCommand = if (Bukkit.getServer().name == "PaperPlane") {
            WrappedBukkitCommand(LiteralArgumentBuilder.literal("@${root.matcher}"))
        } else {
            WrappedBukkitCommand(LiteralArgumentBuilder.literal(root.matcher))
        }
        for (x in command.getCommands()) {
            println("- Baking subcommand matcher $x")
            wrappedCommand.then(bakeBukkitAutoComplete(x, command.getCommand(x)!!, wrappedCommand))
        }
        if (command.hasCommandRunner()) {
            println("- Appending runner at root command $root")
            wrappedCommand.executes {
//                command.execute(it)
                execute(it)
            }
        }
        return wrappedCommand
    }

    fun bakeBukkitAutoComplete(
        current: CommandMatcher,
        storage: CommandStorage<CommandSender>,
        wrappedBukkitCommand: WrappedBukkitCommand
    ): WrappedBukkitCommand {
        println("-- Baking command $current (Has runner: ${storage.hasCommandRunner()})")
        val provider = autoCompleteProvider[current::class.java]
            ?: throw IllegalArgumentException("No provider found for ${current::class.java.name}")
        return provider(wrappedBukkitCommand, current, storage).also { command ->
            if (storage.hasCommandRunner()) {
                println("-- Baked command $current (Runner attached)")
                command.executes {
//                    storage.execute(it)
                    execute(it)
                    Command.SINGLE_SUCCESS
                }
            } else {
                println("-- Baked command $current")
            }
        }
    }


    override fun registerAll(
        owner: KClass<out Any>,
        commandMatchers: Array<CommandMatcher>,
        runner: (CommandArgs<CommandSender>) -> Unit,
        index: Int
    ) {
        if (commandMatchers[0] !is StrictCommandMatcher)
            throw IllegalArgumentException("First argument must be StrictCommandMatcher.")
        val command = (commandMatchers[0] as StrictCommandMatcher).matcher
        val wrappedCommand =
            commands.getOrPut(command) { WrappedBukkitCommand(LiteralArgumentBuilder.literal(command)) }
        super.registerAll(owner, commandMatchers, runner, index)
        if (requireBakeCommand) {
            dispatcher.register(wrappedCommand.literal())
        }
    }

    internal fun bakeAll() {
        for (x in getCommands()) {
            bake(x as StrictCommandMatcher)
        }
    }

    private fun bake(root: StrictCommandMatcher) {
        // TODO : Use reflection to remove existing child node
        commands[root.matcher] = bakeBukkitAutoComplete(root)
        dispatcher.register(commands[root.matcher]!!.literal())
        println("Baked command ${root.matcher}")

    }

    private fun extractDispatcher(): CommandDispatcher<Any> {
        val bukkitDispatcherClass = Class.forName("net.minecraft.commands.CommandDispatcher")
        val brigadierDispatcherClass = Class.forName("com.mojang.brigadier.CommandDispatcher")
        val serverInstance = Bukkit.getServer().executeEmptyFunction("getServer")
        val extractorMethod = serverInstance.javaClass.methods.filter { it.returnType == bukkitDispatcherClass }
        val bukkitDispatcherInstance = extractorMethod[0].invoke(serverInstance)
        val brigadierDispatcherField =
            bukkitDispatcherInstance.javaClass.declaredFields.filter { it.type == brigadierDispatcherClass }[0]
        brigadierDispatcherField.isAccessible = true
        return brigadierDispatcherField.get(bukkitDispatcherInstance) as CommandDispatcher<Any>
    }

    private fun Any.executeEmptyFunction(name: String): Any {
        return javaClass.getMethod(name).invoke(this)
    }

    private fun Any.extractField(name: String): Any {
        return javaClass.unlockField(name).get(this)
    }

    private fun Class<*>.unlockField(name: String): Field {
        return getDeclaredField(name).apply {
            isAccessible = true
        }
    }

}