package skywolf46.twilightsatellite.velocity.listener.packet

import skywolf46.atmospherereentry.api.packetbridge.annotations.ServerPacketListener
import skywolf46.atmospherereentry.api.packetbridge.data.PacketWrapper
import skywolf46.atmospherereentry.api.packetbridge.packets.GenericPacketResult
import skywolf46.atmospherereentry.api.packetbridge.packets.server.event.PacketEventServerIdentified
import skywolf46.twilightsatellite.common.annotations.PacketListenerContainer
import skywolf46.twilightsatellite.common.packet.PacketCheckPluginExists
import skywolf46.twilightsatellite.velocity.TwilightSatellite
import kotlin.jvm.optionals.getOrNull

@PacketListenerContainer
class PacketListener {
    @ServerPacketListener
    fun onServerIdentified(packet: PacketEventServerIdentified) {
        println("Try to identify server ${packet.connection.getIdentify()}")
        TwilightSatellite.instance.triggerIdentificationFor(packet.connection)
    }

    @ServerPacketListener
    fun PacketWrapper<PacketCheckPluginExists>.onCheckPluginExists() {
        val plugin = TwilightSatellite.instance.server.pluginManager.getPlugin(this.packet.pluginName).getOrNull()
        if(plugin == null) {
            reply(GenericPacketResult(false))
            return
        }

    }
}