package skywolf46.twilightsatellite.bukkit.util

import arrow.core.Option
import arrow.core.toOption

@Deprecated(
    "Feature moved to common module; Use skywolf46.twilightsatellite.common.utility package instead",
    ReplaceWith("skywolf46.twilightsatellite.common.utility.*"),
    DeprecationLevel.WARNING
)
open class SimpleStorage<K : Any, V : Any> {
    private val map = mutableMapOf<K, V>()

    fun put(k: K, v: V) {
        map[k] = v
    }

    fun get(k: K): Option<V> {
        return map[k].toOption()
    }

}