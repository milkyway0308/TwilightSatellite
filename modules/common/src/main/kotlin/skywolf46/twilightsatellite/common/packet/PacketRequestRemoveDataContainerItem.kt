package skywolf46.twilightsatellite.common.packet

import skywolf46.atmospherereentry.api.packetbridge.PacketBase
import skywolf46.atmospherereentry.api.packetbridge.annotations.ReflectedSerializer

@ReflectedSerializer
class PacketRequestRemoveDataContainerItem(val id: List<String>, val key: String) : PacketBase {
    constructor(id: String, key: String) : this(listOf(id), key)
}