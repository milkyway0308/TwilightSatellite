package skywolf46.twilightsatellite.bukkit.commands

import kotlin.reflect.KClass

open class CommandStorage<LISTENER : Any>(val owner: KClass<out Any>) {
    private val commands = mutableMapOf<CommandMatcher, CommandStorage<LISTENER>>()
    private var reservedCommandRunner: ((CommandArgs<LISTENER>) -> Unit)? = null
    private val restrictions = mutableListOf<(CommandMatcher) -> Unit>()

    fun addRestriction(restriction: (CommandMatcher) -> Unit) {
        restrictions.add(restriction)
    }

    fun getAllMatchers(): List<CommandMatcher> {
        return commands.keys.toList()
    }

    fun hasCommandRunner() = reservedCommandRunner != null

    open fun registerAll(
        owner: KClass<out Any>,
        commandMatchers: Array<CommandMatcher>,
        runner: (CommandArgs<LISTENER>) -> Unit,
        index: Int
    ) {
        restrictions.forEach {
            it(commandMatchers[index])
        }
        if (commandMatchers.size == index) {
            this.reservedCommandRunner = runner
            return
        }
        commands.getOrPut(commandMatchers[index]) { CommandStorage(owner) }
            .registerAll(owner, commandMatchers, runner, index + 1)
    }

    fun execute(args: CommandArgs<*>, index: Int = 0) {
        if (!args.hasNext()) {
            reservedCommandRunner?.invoke(args as CommandArgs<LISTENER>)
            return
        }
        for ((k, v) in commands) {
            if (k.isMatchedWith(args.clone())) {
                k.parseCommandArg(args)
                v.execute(args, index + 1)
                return
            }
        }
        reservedCommandRunner?.invoke(args as CommandArgs<LISTENER>)
    }

    fun getCommands(): List<CommandMatcher> {
        return commands.keys.toList()
    }

    fun getCommand(matcher: CommandMatcher): CommandStorage<LISTENER>? {
        return commands[matcher]
    }
}