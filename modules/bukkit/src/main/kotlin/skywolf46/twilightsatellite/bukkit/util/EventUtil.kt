package skywolf46.twilightsatellite.bukkit.util

import org.bukkit.Bukkit
import org.bukkit.event.Event
import skywolf46.twilightsatellite.common.data.collections.SingleConsumer

object EventUtil {
    @JvmStatic
    fun <T: Event> invokeEvent(event: T): T {
        return event.apply {
            Bukkit.getPluginManager().callEvent(this)
        }
    }

    @JvmStatic
    fun <T: Event> invokeEvent(event: T, unit: SingleConsumer<T>) {
        invokeEvent(event).apply {
            unit.accept(this)
        }
    }
}
fun <T : Event> T.invokeEvent(): T {
    return this.apply {
        Bukkit.getPluginManager().callEvent(this)
    }
}

inline fun <reified T : Event> T.invokeEvent(unit: (T) -> Unit) {
    invokeEvent().apply(unit)
}