package skywolf46.twilightsatellite.bukkit.data.container

import skywolf46.atmospherereentry.api.packetbridge.PacketBase
import skywolf46.atmospherereentry.api.packetbridge.PacketBridgeClient
import skywolf46.atmospherereentry.api.packetbridge.packets.GenericPacketResult
import skywolf46.atmospherereentry.api.packetbridge.waitReplyOf
import skywolf46.twilightsatellite.bukkit.TwilightSatellite
import skywolf46.twilightsatellite.common.data.Snapshotable
import skywolf46.twilightsatellite.common.data.container.DataContainer
import skywolf46.twilightsatellite.common.packet.*

class InternalComplexDataContainer<V : Snapshotable> internal constructor(
    private val id: List<String>,
    private val client: PacketBridgeClient = TwilightSatellite.instance.client
) : DataContainer<String, V> {
    override fun acquire(key: String, accessor: (V) -> Unit): DataContainer<String, V> {
        client.waitReplyOf<PacketBase>(PacketRequestDataContainerItem(id, key)) {
            if (it is PacketUnknownContainer) {
                throw IllegalStateException("Tried to access non-existing data container. ($id : ${key})")
            } else {
                it as PacketResponseDataContainerItem<Snapshotable>
                accessor.invoke(it.item as V)
            }
        }
        return this
    }

    override fun modify(key: String, modifier: (V) -> V): DataContainer<String, V> {
        acquire(key) {
            client.send(PacketRequestModifyDataContainer(id, key, modifier.invoke(it)))
        }
        return this
    }

    override fun update(key: String, value: V): DataContainer<String, V> {
        client.send(PacketRequestModifyDataContainer(id, key, value))
        return this
    }

    override fun contains(key: String, accessor: (Boolean) -> Unit): DataContainer<String, V> {
        client.waitReplyOf<PacketBase>(PacketRequestDataContainerItem(id, key)) {
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
        client.waitReplyOf<PacketResponseDataContainerKeys>(PacketRequestContainerKeys(id)) {
            accessor.invoke(it.keys)
        }
        return this
    }

    override fun listValues(accessor: (List<V>) -> Unit): DataContainer<String, V> {
        client.waitReplyOf<PacketResponseDataContainerValues<V>>(PacketRequestContainerValues(id)) {
            accessor.invoke(it.values)
        }
        return this
    }

    override fun entries(accessor: (Map<String, V>) -> Unit): DataContainer<String, V> {
        client.waitReplyOf<PacketResponseDataContainerEntries<V>>(PacketRequestContainerEntries(id)) {
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
        client.send(PacketRequestRemoveDataContainerItem(id, key))
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