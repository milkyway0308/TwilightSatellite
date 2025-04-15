package skywolf46.twilightsatellite.bukkit.commands.consumer

import skywolf46.twilightsatellite.bukkit.annotations.commands.AutoArgumentConsumer
import skywolf46.twilightsatellite.bukkit.commands.ArgumentConsumer
import skywolf46.twilightsatellite.bukkit.commands.FilterableIterator

@AutoArgumentConsumer
class StrictStringConsumer : ArgumentConsumer {
    override fun canConsume(iterator: FilterableIterator<Char>): Boolean {
        return true
    }

    override fun consume(iterator: FilterableIterator<Char>): String {
        return String(iterator.nextUntil { it != ' ' }.toCharArray())
    }

    override fun priority(): Int {
        return Integer.MIN_VALUE
    }
}