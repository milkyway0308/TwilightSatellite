package skywolf46.twilightsatellite.bukkit.packet

import org.bukkit.Bukkit
import skywolf46.atmospherereentry.api.packetbridge.data.PacketWrapper
import skywolf46.atmospherereentry.api.packetbridge.packets.server.PacketIdentifyComplete
import skywolf46.atmospherereentry.api.packetbridge.packets.server.PacketIdentifyDenied
import skywolf46.twilightsatellite.bukkit.TwilightSatellite
import skywolf46.twilightsatellite.common.annotations.ClientPacketListener
import skywolf46.twilightsatellite.common.annotations.PacketListenerContainer

@PacketListenerContainer
class PacketListener {
    @ClientPacketListener
    fun onPacket(packet: PacketIdentifyComplete) {
        Bukkit.getConsoleSender()
            .sendMessage("§eTwilightSatellite §7| §fServer identified. Your server name is ${packet.serverId}")
        TwilightSatellite.instance.identifiedName = packet.serverId
    }

    @ClientPacketListener
    fun onPacket(packet: PacketIdentifyDenied) {
        Bukkit.getConsoleSender()
            .sendMessage("§eTwilightSatellite §7| §fServer identification denied. Reason : ${packet.cause}")
    }

    @ClientPacketListener
    fun onIdentified(packet: PacketWrapper<PacketIdentifyComplete>) {
        Bukkit.getConsoleSender().sendMessage("§eTwilightSatellite §7| §fTest listener.")
    }

    @ClientPacketListener
    fun onIdentifyDenied(packet: PacketWrapper<PacketIdentifyDenied>) {
        Bukkit.getConsoleSender()
            .sendMessage("§eTwilightSatellite §7| §cPacket denied. Reason : ${packet.packet.cause}")
    }
}