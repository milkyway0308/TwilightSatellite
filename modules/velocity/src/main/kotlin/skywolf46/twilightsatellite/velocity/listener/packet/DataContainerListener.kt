package skywolf46.twilightsatellite.velocity.listener.packet

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import skywolf46.atmospherereentry.api.packetbridge.annotations.PacketListener
import skywolf46.atmospherereentry.api.packetbridge.annotations.ServerPacketListener
import skywolf46.atmospherereentry.api.packetbridge.data.PacketWrapper
import skywolf46.atmospherereentry.api.packetbridge.packets.GenericPacketResult
import skywolf46.twilightsatellite.common.annotations.PacketListenerContainer
import skywolf46.twilightsatellite.common.annotations.ProxyPacketListener
import skywolf46.twilightsatellite.common.data.Snapshotable
import skywolf46.twilightsatellite.common.packet.*
import skywolf46.twilightsatellite.velocity.data.container.ProxyDataListenerContainer

@PacketListenerContainer
class DataContainerListener : KoinComponent {
    private val container by inject<ProxyDataListenerContainer>()

    @ProxyPacketListener
    fun PacketWrapper<PacketRequestDataContainerItem>.onRequested() {
        container.processGetRequest<String, Snapshotable>(packet.id, packet.key) {
            if (it == null) {
                reply(PacketUnknownContainer(packet.id))
            } else {
                reply(PacketResponseDataContainerItem(packet.id, packet.key, it))
            }
        }
    }

    @ProxyPacketListener
    fun PacketWrapper<PacketRequestModifyDataContainer>.onModifyRequested() {
        container.processModifyRequest(packet.id, packet.key, packet.data) {
            if (!it) {
                System.err.println("Failed process modify request - Container not registered. (${packet.id} : ${packet.key})")
            }
        }
    }

    @ProxyPacketListener
    fun PacketWrapper<PacketRequestCheckContainerDataExists>.onCheckDataExists() {
        container.processCheckRequest<Any, Snapshotable>(packet.id, packet.key) {
            reply(GenericPacketResult(it))
        }
    }

    @ProxyPacketListener
    fun PacketWrapper<PacketRequestRemoveDataContainerItem>.onDeleteRequested() {
        container.processRemoveRequest<Any, Snapshotable>(packet.id, packet.key)
    }


    @ProxyPacketListener
    fun PacketWrapper<PacketRequestContainerKeys>.onKeysRequested() {
        container.processKeyListRequest(packet.id) { keys ->
            reply(PacketResponseDataContainerKeys(packet.id, keys))
        }
    }

    @ProxyPacketListener
    fun PacketWrapper<PacketRequestContainerValues>.onValueRequested() {
        container.processValuesListRequest<String, Snapshotable>(packet.id) { keys ->
            reply(PacketResponseDataContainerValues(packet.id, keys))
        }
    }

    @ProxyPacketListener
    fun PacketWrapper<PacketRequestContainerEntries>.onEntryRequested() {
        container.processEntriesRequest<String, Snapshotable>(packet.id) { entries ->
            reply(PacketResponseDataContainerEntries(packet.id, entries))
        }
    }




}