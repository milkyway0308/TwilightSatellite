package skywolf46.twilightsatellite.bukkit.commands.bukkit.conditions

import skywolf46.twilightsatellite.bukkit.commands.CommandArgs
import skywolf46.twilightsatellite.bukkit.commands.Precondition

class AnyCondition<T : Any>(vararg val conditions: Precondition<T>) : Precondition<T>() {
    override fun check(args: CommandArgs<T>): Boolean {
        return conditions.any { it.check(args) }
    }

    override fun checkSilent(args: CommandArgs<T>): Boolean {
        return conditions.any { it.checkSilent(args) }
    }
}