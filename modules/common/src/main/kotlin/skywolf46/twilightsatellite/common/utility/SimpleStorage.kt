package skywolf46.twilightsatellite.common.utility

import arrow.core.Option
import arrow.core.toOption

open class SimpleStorage<K : Any, V : Any> {
    private val map = mutableMapOf<K, V>()

    fun put(k: K, v: V) {
        map[k] = v
    }

    fun get(k: K): Option<V> {
        return map[k].toOption()
    }

}