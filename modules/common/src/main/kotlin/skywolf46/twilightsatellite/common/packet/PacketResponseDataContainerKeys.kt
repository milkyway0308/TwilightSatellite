package skywolf46.twilightsatellite.common.packet

import skywolf46.atmospherereentry.api.packetbridge.PacketBase
import skywolf46.atmospherereentry.api.packetbridge.annotations.ReflectedSerializer
import skywolf46.twilightsatellite.common.data.Snapshotable

@ReflectedSerializer
class PacketResponseDataContainerKeys(val id: List<String>, val keys: List<String>) : PacketBase {
    constructor(id: String, keys: List<String>) : this(listOf(id), keys)
}