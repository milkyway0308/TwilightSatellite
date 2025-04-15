package skywolf46.twilightsatellite.bukkit.commands.matcher

import arrow.core.Option
import arrow.core.toOption
import skywolf46.twilightsatellite.bukkit.annotations.commands.AutoCommandMatcher
import skywolf46.twilightsatellite.bukkit.commands.CommandArgs
import skywolf46.twilightsatellite.bukkit.commands.CommandMatcher
import skywolf46.twilightsatellite.bukkit.commands.FilterableIterator

@Suppress("unused")
@AutoCommandMatcher
class StrictCommandMatcher(val matcher: String) : CommandMatcher {
    constructor() : this("")

    override fun createFrom(args: FilterableIterator<Char>): Option<CommandMatcher> {
        args.until { it != ' ' }
        return StrictCommandMatcher(String(args.nextUntil { it == ' ' }.toCharArray())).toOption()
    }

    override fun isMatchedWith(args: CommandArgs<*>): Boolean {
        return args.next() == matcher
    }

    override fun requireCompleteEvent(): Boolean {
        return false
    }

    override fun parseCommandArg(args: CommandArgs<*>) {
        args.next()
    }

    override fun toString(): String {
        return matcher
    }

    override fun equals(other: Any?): Boolean {
        return other is StrictCommandMatcher && other.matcher == matcher
    }

    override fun hashCode(): Int {
        return arrayOf(this.javaClass.name, matcher).contentHashCode()
    }
}