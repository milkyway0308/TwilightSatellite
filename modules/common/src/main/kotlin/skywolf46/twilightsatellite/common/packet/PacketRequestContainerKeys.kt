package skywolf46.twilightsatellite.common.packet

import skywolf46.atmospherereentry.api.packetbridge.PacketBase
import skywolf46.atmospherereentry.api.packetbridge.annotations.ReflectedSerializer

@ReflectedSerializer
class PacketRequestContainerKeys(val id: List<String>) : PacketBase {
    constructor(id: String) : this(listOf(id))
}