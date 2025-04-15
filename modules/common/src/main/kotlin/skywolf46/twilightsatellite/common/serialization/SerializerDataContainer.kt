package skywolf46.twilightsatellite.common.serialization

import java.io.File
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class SerializerDataContainer<KEY : Any, VALUE : Any>(
    // Target directory
    val directory: File,
    // Do we need to load all data when instance created?
    val forceLoadAllData: Boolean,
    // Delay max 15 seconds when data updated
    val saveDelay: Long = 15000L,
    // Force save interval.
    // If it's not -1, will force save data to disk every interval.
    val forceSaveInterval: Long = -1L,
    val emptyValueProvider: (KEY) -> VALUE
) {
    private val cache = mutableMapOf<KEY, FlaggedValue<KEY, VALUE>>()
    private val lock = ReentrantReadWriteLock()

    fun acquire(key: KEY): VALUE {
        val value = lock.read {
            cache[key]
        }
        if (value == null) {
            val newValue = emptyValueProvider(key)
            lock.write {
                cache[key] = FlaggedValue(key, newValue)
            }
            return newValue
        }
        return value.value
    }


    fun save(key: KEY) {
        cache[key]?.saveAt = System.currentTimeMillis()
    }

    data class FlaggedValue<KEY, VALUE>(
        val key: KEY,
        val value: VALUE,
        var saveAt: Long = -1L
    )
}