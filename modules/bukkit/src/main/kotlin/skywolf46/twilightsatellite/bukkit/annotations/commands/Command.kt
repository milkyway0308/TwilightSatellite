package skywolf46.twilightsatellite.bukkit.annotations.commands

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command(vararg val commands: String = [])

