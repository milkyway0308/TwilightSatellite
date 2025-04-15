package skywolf46.twilightsatellite.common.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ClientPacketListener(val priority: Int = 0)