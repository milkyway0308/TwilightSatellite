package skywolf46.twilightsatellite.bukkit.commands.exceptions

class ConditionMismatchedException : RuntimeException() {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}