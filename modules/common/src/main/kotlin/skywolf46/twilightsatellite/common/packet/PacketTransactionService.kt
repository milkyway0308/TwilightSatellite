package skywolf46.twilightsatellite.common.packet

import skywolf46.atmospherereentry.api.packetbridge.annotations.ReflectedSerializer
import skywolf46.atmospherereentry.api.packetbridge.data.DoubleHashedType

@ReflectedSerializer
class PacketTransactionService(val targetCLass: DoubleHashedType, val packets : ByteArray) {

}