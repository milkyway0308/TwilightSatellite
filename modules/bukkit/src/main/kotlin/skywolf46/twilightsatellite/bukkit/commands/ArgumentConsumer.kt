package skywolf46.twilightsatellite.bukkit.commands

interface ArgumentConsumer {
    fun canConsume(iterator: FilterableIterator<Char>): Boolean

    fun consume(iterator: FilterableIterator<Char>): String

    fun priority(): Int = 0
}