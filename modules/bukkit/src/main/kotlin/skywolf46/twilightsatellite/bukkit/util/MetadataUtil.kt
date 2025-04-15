package skywolf46.twilightsatellite.bukkit.util

import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.Metadatable
import org.bukkit.plugin.java.JavaPlugin

fun <T : Any> Metadatable.metadata(key: String): T? {
    return getMetadata(key).firstOrNull()?.value() as? T
}

fun Metadatable.metadata(plugin: JavaPlugin, key: String, value: Any) {
    setMetadata(key, FixedMetadataValue(plugin, value))
}

fun Metadatable.removeMetadata(plugin: JavaPlugin, key: String) {
    removeMetadata(key, plugin)
}