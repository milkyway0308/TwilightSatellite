package skywolf46.twilightsatellite.bukkit.data.container

import skywolf46.atmospherereentry.api.packetbridge.PacketBase
import skywolf46.atmospherereentry.api.packetbridge.PacketBridgeClient
import skywolf46.atmospherereentry.api.packetbridge.packets.GenericPacketResult
import skywolf46.atmospherereentry.api.packetbridge.waitReplyOf
import skywolf46.twilightsatellite.bukkit.TwilightSatellite
import skywolf46.twilightsatellite.common.data.Snapshotable
import skywolf46.twilightsatellite.common.data.container.DataContainer
import skywolf46.twilightsatellite.common.packet.*

class ProxyBasedComplexDataContainer<VALUE : Snapshotable>(
    private val id: String,
    private val client: PacketBridgeClient = TwilightSatellite.instance.client
) : DataContainer<String, DataContainer<String, VALUE>> {
    private val container = mutableMapOf<String, InternalComplexDataContainer<VALUE>>()
    override fun acquire(
        key: String,
        accessor: (DataContainer<String, VALUE>) -> Unit
    ): DataContainer<String, DataContainer<String, VALUE>> {
        accessor(container.getOrPut(key) { InternalComplexDataContainer(listOf(id, key)) })
        return this
    }

    override fun modify(
        key: String,
        modifier: (DataContainer<String, VALUE>) -> DataContainer<String, VALUE>
    ): DataContainer<String, DataContainer<String, VALUE>> {
        throw IllegalStateException("Complex data container does not support modification.")
    }

    override fun update(
        key: String,
        value: DataContainer<String, VALUE>
    ): DataContainer<String, DataContainer<String, VALUE>> {
        throw IllegalStateException("Complex data container does not support modification.")
    }

    override fun contains(
        key: String,
        accessor: (Boolean) -> Unit
    ): DataContainer<String, DataContainer<String, VALUE>> {
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

    override fun listKeys(accessor: (List<String>) -> Unit): DataContainer<String, DataContainer<String, VALUE>> {
        client.waitReplyOf<PacketResponseDataContainerKeys>(PacketRequestContainerKeys(id)) {
            accessor.invoke(it.keys)
        }
        return this
    }

    override fun detailedModify(
        key: String,
        modifier: (DataContainer<String, VALUE>) -> DataContainer.ModificationOperation<DataContainer<String, VALUE>>
    ): DataContainer<String, DataContainer<String, VALUE>> {
        TODO("Not supported in proxy-based data container")
    }

    override fun remove(key: String): DataContainer<String, DataContainer<String, VALUE>> {
        client.send(PacketRequestRemoveDataContainerItem(id, key))
        return this
    }

    override fun flag(key: String): DataContainer<String, DataContainer<String, VALUE>> {
        // Not supported in proxy-based data container
        return this
    }

    override fun deflag(key: String): DataContainer<String, DataContainer<String, VALUE>> {
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