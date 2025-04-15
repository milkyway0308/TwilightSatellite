package skywolf46.twilightsatellite.common.packet

import skywolf46.atmospherereentry.api.packetbridge.PacketBase
import skywolf46.atmospherereentry.api.packetbridge.annotations.ReflectedSerializer
import skywolf46.twilightsatellite.common.data.Snapshotable

@ReflectedSerializer
class PacketResponseDataContainerValues<T: Snapshotable>(val id: List<String>, val values: List<T>) : PacketBase {
    constructor(id: String, values: List<T>) : this(listOf(id), values)
}