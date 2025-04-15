package skywolf46.twilightsatellite.velocity.data.container

import skywolf46.twilightsatellite.common.data.Snapshotable
import skywolf46.twilightsatellite.common.data.container.ComplexDataContainer
import skywolf46.twilightsatellite.common.data.container.DataContainer
import java.util.concurrent.Executors

class ProxyDataListenerContainer {
    private val dataUpdateThread = Executors.newSingleThreadExecutor()

    private val reservedContainers = mutableMapOf<String, DataContainer<out Any, out Any>>()

    fun bindContainer(id: String, container: DataContainer<out Any, out Any>) {
        reservedContainers[id] = container
    }


    fun <K : Any, V : Snapshotable> processGetRequest(id: List<String>, key: K, unit: (V?) -> Unit) {
        dataUpdateThread.submit {
            acquireContainer(id) {
                if (it == null) {
                    unit.invoke(null)
                    return@acquireContainer
                }

                it.acquire(key) { data ->
                    unit.invoke(data)
                }
            }
        }
    }


    fun <K : Any> processKeyListRequest(id: List<String>, unit: (List<K>) -> Unit) {
        dataUpdateThread.submit {
            acquireContainer<K, Any>(id) {
                if (it == null) {
                    // TODO : Add emptyList() implementation
                    unit.invoke(ArrayList())
                    return@acquireContainer
                }
                it.listKeys { keys ->
                    unit.invoke(keys)
                }
            }
        }
    }


    fun <K : Any, V : Snapshotable> processValuesListRequest(id: List<String>,  unit: (List<V>) -> Unit) {
        dataUpdateThread.submit {
            acquireContainer<K, V>(id) {
                if (it == null) {
                    unit.invoke(ArrayList())
                    return@acquireContainer
                }

                it.listValues { values ->
                    unit.invoke(values)
                }
            }
        }
    }
    fun <K : Any, V : Snapshotable> processEntriesRequest(id: List<String>,  unit: (LinkedHashMap<K, V>) -> Unit) {
        dataUpdateThread.submit {
            acquireContainer<K, V>(id) {
                if (it == null) {
                    unit.invoke(java.util.LinkedHashMap())
                    return@acquireContainer
                }

                it.entries { entries ->
                    // TODO : Prevent map copy to reduce memory usage
                    unit.invoke(java.util.LinkedHashMap(entries))
                }
            }
        }
    }


    fun <K : Any, V : Snapshotable> processModifyRequest(
        id: List<String>, key: K, modifier: V, successListener: (Boolean) -> Unit
    ) {
        dataUpdateThread.submit {
            acquireContainer(id) {
                if (it == null) {
                    successListener.invoke(false)
                    return@acquireContainer
                }
                it.update(key, modifier)
                successListener.invoke(true)
            }
        }
    }

    fun <K : Any, V : Snapshotable> processCheckRequest(id: List<String>, key: K, unit: (Boolean) -> Unit) {
        dataUpdateThread.submit {
            acquireContainer<K, V>(id) {
                if (it == null) {
                    unit.invoke(false)
                    return@acquireContainer
                }
                it.contains(key) { result ->
                    unit.invoke(result)
                }
            }
        }
    }


    fun <K : Any, V : Snapshotable> processRemoveRequest(id: List<String>, key: K) {
        dataUpdateThread.submit {
            acquireContainer<K, V>(id) {
                if (it == null) {
                    return@acquireContainer
                }
                it.remove(key)
            }
        }
    }

    fun <K : Any, V : Any> acquireContainer(id: List<String>, unit: (DataContainer<K, V>?) -> Unit) {
        val container = reservedContainers[id.firstOrNull()] ?: return unit.invoke(null)
        if (id.size == 1) {
            if (container is ComplexDataContainer<*>) return unit.invoke(null)
            return unit.invoke(container as? DataContainer<K, V>)
        }
        if (container !is ComplexDataContainer<*>) return unit.invoke(null)
        container.acquire(id[1]) { data ->
            unit.invoke(data as? DataContainer<K, V>)
        }
    }

}