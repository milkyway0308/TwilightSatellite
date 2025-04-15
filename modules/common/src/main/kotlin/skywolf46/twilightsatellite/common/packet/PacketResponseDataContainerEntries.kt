package skywolf46.twilightsatellite.common.packet

import skywolf46.atmospherereentry.api.packetbridge.PacketBase
import skywolf46.atmospherereentry.api.packetbridge.annotations.ReflectedSerializer
import skywolf46.twilightsatellite.common.data.Snapshotable

@ReflectedSerializer
class PacketResponseDataContainerEntries<T: Snapshotable>(val id: List<String>, val entries: LinkedHashMap<String, T>) : PacketBase {
    constructor(id: String, entries: LinkedHashMap<String, T>) : this(listOf(id), entries)
}