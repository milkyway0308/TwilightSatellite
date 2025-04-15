package skywolf46.twilightsatellite.bukkit.entrypoint

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import skywolf46.atmospherereentry.common.api.annotations.ClassLoaderContainer
import skywolf46.atmospherereentry.common.api.annotations.ClassLoaderWorker
import skywolf46.atmospherereentry.common.api.annotations.IgnoreException

@ClassLoaderContainer
class BukkitClassLoaderContainer {
    @ClassLoaderWorker
    @IgnoreException
    fun getClassLoaders(): List<ClassLoader> {
        println("Class loader impact")
        // ClassLoaderWorker is a special annotation that will be processed by AtmosphereReentry.
        // This annotation will be processed by AtmosphereReentry and will be called when class loader is loaded.
        // This annotation is useful when you want to load class loader from other modules.
        // For example, if you want to load class loader from Velocity module, you can use this annotation to load class loader from Velocity module.
        // This annotation will be called before entry point is called.
        return Bukkit.getPluginManager().plugins.map { (it as JavaPlugin).javaClass.classLoader }
    }
}