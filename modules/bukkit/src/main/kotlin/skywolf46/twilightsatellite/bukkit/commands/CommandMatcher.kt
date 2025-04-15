package skywolf46.twilightsatellite.bukkit.commands

import arrow.core.Option

interface CommandMatcher {

    fun createFrom(args: FilterableIterator<Char>): Option<CommandMatcher>

    fun isMatchedWith(args: CommandArgs<*>): Boolean

    fun getCommandName(): String? = null

    // Higher priority will be executed first
    fun getPriority(): Int = Integer.MIN_VALUE

    fun canUsedInFirstArgument(): Boolean = true

    fun requireCompleteEvent() : Boolean

    fun parseCommandArg(args: CommandArgs<*>)
}