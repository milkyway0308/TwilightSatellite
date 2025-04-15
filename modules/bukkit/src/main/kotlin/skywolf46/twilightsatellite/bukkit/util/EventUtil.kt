package skywolf46.twilightsatellite.bukkit.util

import org.bukkit.Bukkit
import org.bukkit.event.Event

fun <T : Event> T.invokeEvent(): T {
    return this.apply {
        Bukkit.getPluginManager().callEvent(this)
    }
}

inline fun <reified T : Event> T.invokeEvent(unit: (T) -> Unit) {
    invokeEvent().apply(unit)
}