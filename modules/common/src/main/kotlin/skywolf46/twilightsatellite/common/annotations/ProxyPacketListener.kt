package skywolf46.twilightsatellite.common.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ProxyPacketListener(val priority: Int = 0)