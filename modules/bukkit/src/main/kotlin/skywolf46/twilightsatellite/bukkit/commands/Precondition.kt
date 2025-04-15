package skywolf46.twilightsatellite.bukkit.commands

abstract class Precondition<T : Any> {
    abstract fun check(args: CommandArgs<T>): Boolean

    open fun checkSilent(args: CommandArgs<T>): Boolean {
        return check(args)
    }
}