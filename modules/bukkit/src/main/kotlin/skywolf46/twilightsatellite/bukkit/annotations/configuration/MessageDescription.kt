package skywolf46.twilightsatellite.bukkit.annotations.configuration

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class MessageDescription(vararg val description: String)