package skywolf46.twilightsatellite.common.packet

import skywolf46.atmospherereentry.api.packetbridge.PacketBase
import skywolf46.atmospherereentry.api.packetbridge.annotations.ReflectedSerializer
import skywolf46.twilightsatellite.common.data.Snapshotable

@ReflectedSerializer
class PacketResponseDataContainerItem<T: Snapshotable>(val id: List<String>, val key: String, val item: T) : PacketBase {
    constructor(id: String, key: String, item: T) : this(listOf(id), key, item)
}