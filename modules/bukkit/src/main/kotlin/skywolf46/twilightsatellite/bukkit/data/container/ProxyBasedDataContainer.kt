package skywolf46.twilightsatellite.bukkit.data.container

import skywolf46.atmospherereentry.api.packetbridge.PacketBase
import skywolf46.atmospherereentry.api.packetbridge.PacketBridgeClient
import skywolf46.atmospherereentry.api.packetbridge.packets.GenericPacketResult
import skywolf46.atmospherereentry.api.packetbridge.waitReplyOf
import skywolf46.twilightsatellite.bukkit.TwilightSatellite
import skywolf46.twilightsatellite.common.data.container.ObserverAttachableContainer
import skywolf46.twilightsatellite.common.data.container.AttachableContainerObserver
import skywolf46.twilightsatellite.common.data.Snapshotable
import skywolf46.twilightsatellite.common.data.container.DataContainer
import skywolf46.twilightsatellite.common.packet.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class ProxyBasedDataContainer<V : Snapshotable>(
    private val id: String,
    private val client: PacketBridgeClient = TwilightSatellite.instance.client
) : DataContainer<String, V>, ObserverAttachableContainer<String, V> {

    private val observers = mutableListOf<(String, V) -> Unit>()

    private val observerLock = ReentrantReadWriteLock()

    private val sharedObserver = AttachableContainerObserver(this)

    override fun requestAcquire(key: String) {
        acquire(key) {}
    }

    override fun attachObserver(observer: (String, V) -> Unit) {
        observerLock.write {
            observers.add(observer)
        }
    }

    private fun notifyObservers(key: String, value: V) {
        observerLock.read {
            observers.forEach {
                it.invoke(key, value)
            }
        }
    }

    override fun getSharedObserver(): AttachableContainerObserver<String, V> {
        return sharedObserver
    }


    override fun acquire(key: String, accessor: (V) -> Unit): DataContainer<String, V> {
        client.waitReplyOf<PacketBase>(PacketRequestDataContainerItem(listOf(id), key)) {
            if (it is PacketUnknownContainer) {
                throw IllegalStateException("Tried to access non-existing data container. ($id : ${key})")
            } else {
                it as PacketResponseDataContainerItem<Snapshotable>
                accessor.invoke(it.item as V)
                notifyObservers(key, it.item as V)
            }
        }
        return this
    }

    override fun modify(key: String, modifier: (V) -> V): DataContainer<String, V> {
        acquire(key) {
            val item = modifier.invoke(it)
            client.send(PacketRequestModifyDataContainer(listOf(id), key, item))
            notifyObservers(key, item)
        }
        return this
    }

    override fun update(key: String, value: V): DataContainer<String, V> {
        client.send(PacketRequestModifyDataContainer(listOf(id), key, value))
        notifyObservers(key, value)
        return this
    }

    override fun contains(key: String, accessor: (Boolean) -> Unit): DataContainer<String, V> {
        client.waitReplyOf<PacketBase>(PacketRequestDataContainerItem(listOf(id), key)) {
            if (it is PacketUnknownContainer) {
                accessor.invoke(false)
            } else {
                it as GenericPacketResult
                accessor.invoke(it.success)
            }
        }
        return this
    }

    override fun listKeys(accessor: (List<String>) -> Unit): DataContainer<String, V> {
        client.waitReplyOf<PacketResponseDataContainerKeys>(PacketRequestContainerKeys(listOf(id))) {
            accessor.invoke(it.keys)
        }
        return this
    }

    override fun listValues(accessor: (List<V>) -> Unit): DataContainer<String, V> {
        client.waitReplyOf<PacketResponseDataContainerValues<V>>(PacketRequestContainerValues(listOf(id))) {
            accessor.invoke(it.values)
        }
        return this
    }

    override fun entries(accessor: (Map<String, V>) -> Unit): DataContainer<String, V> {
        client.waitReplyOf<PacketResponseDataContainerEntries<V>>(PacketRequestContainerEntries(listOf(id))) {
            accessor.invoke(it.entries)
        }
        return this
    }

    override fun detailedModify(
        key: String,
        modifier: (V) -> DataContainer.ModificationOperation<V>
    ): DataContainer<String, V> {
        TODO("Not supported in proxy-based data container")
    }

    override fun remove(key: String): DataContainer<String, V> {
        client.send(PacketRequestRemoveDataContainerItem(listOf(id), key))
        return this
    }

    override fun flag(key: String): DataContainer<String, V> {
        // Not supported in proxy-based data container
        return this
    }

    override fun deflag(key: String): DataContainer<String, V> {
        // Not supported in proxy-based data container
        return this
    }

    override fun supportSave(): Boolean {
        return false
    }

    override fun flushSave() {
        // Not supported in proxy-based data container
    }


}