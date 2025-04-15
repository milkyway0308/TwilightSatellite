package skywolf46.twilightsatellite.common.data.container

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * InMemoryDataContainer is a data container that stores data in memory.
 * It's basically a map, but thread safe.
 *
 * InMemoryDataContainer does not support auto-save feature.
 */
open class InMemoryDataContainer<K : Any, V : Any>(
    data: Map<K, V> = emptyMap(),
    private val defaultDataProvider: (K) -> V
) : DataContainer<K, V> {
    private val observer = DirectContainerObserver()

    protected val map = LinkedHashMap<K, V>(data)

    protected val lock = ReentrantReadWriteLock()

    fun getDirectObserver() = observer

    override fun acquire(key: K, accessor: (V) -> Unit): DataContainer<K, V> {
        lock.read {
            if (map.containsKey(key)) {
                accessor(map[key]!!)
                return this
            }
        }
        lock.write {
            map[key] = defaultDataProvider(key)
            accessor(map[key]!!)
        }
        lock.read {
            accessor(map[key]!!)
        }
        return this
    }

    override fun modify(key: K, modifier: (V) -> V): DataContainer<K, V> {
        lock.write {
            map[key] = modifier(map[key] ?: defaultDataProvider(key))
        }
        return this
    }

    override fun update(key: K, value: V): DataContainer<K, V> {
        lock.write {
            map[key] = value
        }
        return this
    }

    override fun contains(key: K, accessor: (Boolean) -> Unit): DataContainer<K, V> {
        lock.read {
            accessor(map.containsKey(key))
        }
        return this
    }


    override fun detailedModify(key: K, modifier: (V) -> DataContainer.ModificationOperation<V>): DataContainer<K, V> {
        lock.write {
            when (val operation = modifier(map[key] ?: defaultDataProvider(key))) {
                DataContainer.ModificationOperation.NO_MODIFY -> return@write
                DataContainer.ModificationOperation.EMPTY -> return@write
                DataContainer.ModificationOperation.REMOVE -> {
                    map.remove(key)
                }

                else -> {
                    map[key] = operation.data
                }
            }

        }
        return this
    }

    override fun flag(key: K): DataContainer<K, V> {
        // Do nothing - InMemoryDataContainer does not support flagging.
        return this
    }

    override fun deflag(key: K): DataContainer<K, V> {
        // Do nothing - InMemoryDataContainer does not support flagging.
        return this
    }

    override fun remove(key: K): DataContainer<K, V> {
        lock.write {
            map.remove(key)
        }
        return this
    }

    override fun listKeys(accessor: (List<K>) -> Unit): DataContainer<K, V> {
        lock.read {
            accessor(map.keys.toList())
        }
        return this
    }

    override fun listValues(accessor: (List<V>) -> Unit): DataContainer<K, V> {
        lock.read {
            accessor(map.values.toList())
        }
        return this
    }

    override fun entries(accessor: (Map<K, V>) -> Unit): DataContainer<K, V> {
        lock.read {
            accessor(map.toMap())
        }
        return this
    }


    override fun supportSave(): Boolean {
        return false
    }

    override fun flushSave() {
        // Do nothing - InMemoryDataContainer does not support auto-save.
    }


    inner class DirectContainerObserver : ContainerObserver<K, V> {
        override fun getValue(key: K): V? {
            return lock.read { map[key] }
        }

        override fun getOrRequestValue(key: K): V? {
            return getValue(key)
        }

        override fun getAndRequestValue(key: K): V? {
            return getValue(key)
        }
    }
}