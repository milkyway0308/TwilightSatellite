package skywolf46.twilightsatellite.velocity.entrypoint

import skywolf46.atmospherereentry.common.api.annotations.ClassLoaderContainer
import skywolf46.atmospherereentry.common.api.annotations.ClassLoaderWorker
import skywolf46.atmospherereentry.common.api.annotations.IgnoreException
import skywolf46.twilightsatellite.velocity.TwilightSatellite
import kotlin.jvm.optionals.getOrNull


@ClassLoaderContainer
class VelocityClassLoaderContainer {
    @ClassLoaderWorker
    @IgnoreException
    fun getClassLoaders() : List<ClassLoader>{
        // ClassLoaderWorker is a special annotation that will be processed by AtmosphereReentry.
        // This annotation will be processed by AtmosphereReentry and will be called when class loader is loaded.
        // This annotation is useful when you want to load class loader from other modules.
        // For example, if you want to load class loader from Velocity module, you can use this annotation to load class loader from Velocity module.
        // This annotation will be called before entry point is called.
        return TwilightSatellite.instance.server.pluginManager.plugins.mapNotNull { it.instance.getOrNull()?.javaClass?.classLoader }
    }
}