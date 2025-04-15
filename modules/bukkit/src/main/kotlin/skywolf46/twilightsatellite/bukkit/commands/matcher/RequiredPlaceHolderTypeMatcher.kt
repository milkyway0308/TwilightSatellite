package skywolf46.twilightsatellite.bukkit.commands.matcher

import arrow.core.None
import arrow.core.Option
import arrow.core.toOption
import skywolf46.twilightsatellite.bukkit.annotations.commands.AutoCommandMatcher
import skywolf46.twilightsatellite.bukkit.commands.CommandArgs
import skywolf46.twilightsatellite.bukkit.commands.CommandMatcher
import skywolf46.twilightsatellite.bukkit.commands.FilterableIterator

@Suppress("unused")
@AutoCommandMatcher
class RequiredPlaceHolderTypeMatcher() : PseudoTypeWrappedMatcher() {
    var completionName: String? = null
        private set
    lateinit var type: String
        private set
    var parameter: String? = null
        private set

    constructor(commandName: String?, type: String, parameter: String?) : this() {
        this.type = type
        this.completionName = commandName
        this.parameter = parameter
    }

    override fun createFrom(args: FilterableIterator<Char>): Option<CommandMatcher> {
        args.until { it != ' ' }
        if (args.next() != '<')
            return None
        var openCount = 1
        val string = StringBuilder()
        var commandName: String? = null
        var commandType: String? = null
        var parameter: String? = null
        while (openCount != 0 && args.hasNext()) {
            val next = args.next()
            when (next) {
                '\\' -> {
                    string.append("\\")
                    if (args.hasNext()) {
                        string.append(args.next())
                    }
                }

                '#' -> {
                    if (commandName == null) {
                        commandName = string.toString()
                        string.clear()
                    } else {
                        string.append('#')
                    }
                }

                ':' -> {
                    if (commandType == null) {
                        commandType = string.toString()
                        string.clear()
                    } else {
                        string.append(':')
                    }
                }

                '<' -> openCount++
                '>' -> openCount--
                else -> string.append(next)
            }
        }
        if (commandType == null) {
            commandType = string.toString()
        } else {
            parameter = string.toString()
        }
        if (openCount != 0) {
            throw IllegalStateException("Required command option not closed")
        }

        if (!super.isTypeExists(commandType)) {
            throw IllegalStateException("Required command option type $string not exists")
        }
        return RequiredPlaceHolderTypeMatcher(commandName, commandType, parameter).toOption()
    }

    override fun isMatchedWith(args: CommandArgs<*>): Boolean {
        return isTypeSatisfied(type, args)
    }

    override fun parseCommandArg(args: CommandArgs<*>) {
        isTypeSatisfied(type, args)
    }

    override fun hashCode(): Int {
        return arrayOf(this.javaClass.name, type).contentHashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is RequiredPlaceHolderTypeMatcher && other.type == type
    }

    override fun getCommandName(): String? {
        if (completionName?.startsWith('!') == true)
            return completionName?.substring(1)
        return completionName
    }


    override fun requireCompleteEvent(): Boolean {
        return completionName?.startsWith('!') == true
    }
}