package skywolf46.twilightsatellite.common.data.container

import skywolf46.twilightsatellite.common.data.DataContainerThreadManager
import skywolf46.twilightsatellite.common.data.Flaggable
import skywolf46.twilightsatellite.common.data.Snapshotable
import skywolf46.twilightsatellite.common.data.codec.DataCodec
import skywolf46.twilightsatellite.common.data.transput.Transputer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write


class FileBaseDataContainer<K : Any, V : Snapshotable>(
    private val option: SerializeOption,
    private val defaultValueProvider: (K) -> V,

    map: Map<K, V> = emptyMap(),
    private val threadManager: DataContainerThreadManager
) : InMemoryDataContainer<K, V>(map, defaultValueProvider), ObserverAttachableContainer<K, V>, Flaggable {


    private val flagMap = LinkedHashMap<K, FlagData<K>>()

    private val flagLock = ReentrantReadWriteLock()


    private val observers = mutableListOf<(K, V) -> Unit>()

    private val observerLock = ReentrantReadWriteLock()

    override fun requestAcquire(key: K) {
        acquire(key) {}
    }

    override fun onFlagTick(): Boolean {
        runCatching {
            flushSave()
        }.onFailure {
            it.printStackTrace()
        }
        return lock.read { flagMap.isNotEmpty() }
    }

    override fun attachObserver(observer: (K, V) -> Unit) {
        observerLock.write {
            observers.add(observer)
        }
    }

    override fun getSharedObserver(): ContainerObserver<K, V> {
        return getDirectObserver()
    }

    override fun acquire(key: K, accessor: (V) -> Unit): DataContainer<K, V> {
        threadManager.submitAccess {
            loadOrCreate(key)
            super.acquire(key, accessor)
        }
        return this
    }


    override fun modify(key: K, modifier: (V) -> V): DataContainer<K, V> {
        threadManager.submitAccess {
            loadOrCreate(key)
            super.modify(key, modifier)
            flag(key)
        }
        return this
    }

    override fun update(key: K, value: V): DataContainer<K, V> {
        threadManager.submitAccess {
            loadOrCreate(key)
            super.update(key, value)
            flag(key)
        }
        return this
    }

    override fun detailedModify(key: K, modifier: (V) -> DataContainer.ModificationOperation<V>): DataContainer<K, V> {
        threadManager.submitAccess {
            loadOrCreate(key)
            super.detailedModify(key, modifier)
            flag(key)
        }
        return this
    }

    override fun remove(key: K): DataContainer<K, V> {
        threadManager.submitAccess {
            loadOrCreate(key)
            super.remove(key)
            deflag(key)
        }
        return this
    }

    override fun flag(key: K): DataContainer<K, V> {
        flagLock.write {
            flagMap[key] = FlagData(key, System.currentTimeMillis() + option.savePeriod)
        }
        threadManager.addFlagTick(this)
        return this
    }

    override fun deflag(key: K): DataContainer<K, V> {
        flagLock.write {
            flagMap.remove(key)
        }
        return this
    }

    override fun listKeys(accessor: (List<K>) -> Unit): DataContainer<K, V> {
        threadManager.submitAccess {
            super.listKeys(accessor)
        }
        return this
    }

    override fun listValues(accessor: (List<V>) -> Unit): DataContainer<K, V> {
        threadManager.submitAccess {
            super.listValues(accessor)
        }
        return this
    }

    override fun entries(accessor: (Map<K, V>) -> Unit): DataContainer<K, V> {
        threadManager.submitAccess {
            super.entries(accessor)
        }
        return this
    }

    private fun loadOrCreate(key: K) {
        if (!super.lock.read { super.map.contains(key) }) {
            lock.write { loadAndContinue(key) }
        }
    }

    // TODO : Fix this poor implemented load logic
    private fun loadAndContinue(key: K) {
        val latch = CountDownLatch(1)
        option.transputer.import(key.toString()) {
            if (it == null) {
                super.map[key] = defaultValueProvider(key)
            } else {
                kotlin.runCatching {
                    super.map[key] = option.codec.deserialize(it) as V
                }.onFailure { it.printStackTrace() }
            }
            latch.countDown()
        }
        latch.await()
    }

    override fun flushSave() {
        val dataToSave = lock.write {
            val data = mutableMapOf<K, V?>()
            super.lock.write {
                for ((k, v) in flagMap) {
                    if (v.saveAt > System.currentTimeMillis()) {
                        continue
                    }
                    data[k] = super.map[k]?.snapshot() as? V
                }
                for ((k, _) in data) {
                    flagMap.remove(k)
                }
            }
            data
        }
        for (x in dataToSave) {
            if (x.value == null) {
                option.transputer.delete(x.key.toString())
            } else {
                option.transputer.export(x.key.toString(), option.codec.serialize(x.value!!))
            }
        }
    }

    override fun shutdown() {
        val dataToSave = lock.write {
            val data = mutableMapOf<K, V?>()
            super.lock.write {
                for ((k, _) in flagMap) {
                    data[k] = super.map[k]?.snapshot() as? V
                }
            }
            flagMap.clear()
            data
        }
        for (x in dataToSave) {
            if (x.value == null) {
                option.transputer.delete(x.key.toString())
            } else {
                option.transputer.export(x.key.toString(), option.codec.serialize(x.value!!))
            }
        }
    }

    override fun forceFinalize() {
        shutdown()
    }

    data class FlagData<K : Any>(val key: K, val saveAt: Long)

    data class SerializeOption(
        val codec: DataCodec,
        val transputer: Transputer<ByteArray>,
        val savePeriod: Long
    )
}

fun <K : Any, V : Snapshotable> DataContainer.Companion.file(
    option: FileBaseDataContainer.SerializeOption,
    map: Map<K, V> = emptyMap(),
    defaultValueProvider: (K) -> V,
    threadManager: DataContainerThreadManager
): DataContainer<K, V> {
    return FileBaseDataContainer(option, defaultValueProvider, map, threadManager)
}

fun <K : Any, V : Snapshotable> DataContainer.Companion.file(
    option: FileBaseDataContainer.SerializeOption,
    map: Map<K, V> = emptyMap(),
    defaultValueProvider: (K) -> V
): DataContainer<K, V> {
    return FileBaseDataContainer(option, defaultValueProvider, map, DataContainerThreadManager(30000L))
}