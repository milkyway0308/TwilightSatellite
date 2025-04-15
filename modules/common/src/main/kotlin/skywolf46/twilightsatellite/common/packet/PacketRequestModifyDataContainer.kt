package skywolf46.twilightsatellite.common.packet

import skywolf46.atmospherereentry.api.packetbridge.PacketBase
import skywolf46.atmospherereentry.api.packetbridge.annotations.ReflectedSerializer
import skywolf46.twilightsatellite.common.data.Snapshotable

@ReflectedSerializer
class PacketRequestModifyDataContainer(val id: List<String>, val key: String, val data: Snapshotable) : PacketBase {
    constructor(id: String, key: String, data: Snapshotable) : this(listOf(id), key, data)
}