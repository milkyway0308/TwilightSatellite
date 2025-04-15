package skywolf46.twilightsatellite.common.serializer

import io.netty.buffer.ByteBuf
import skywolf46.atmospherereentry.api.packetbridge.DataSerializerBase
import skywolf46.atmospherereentry.api.packetbridge.annotations.NetworkSerializer
import skywolf46.atmospherereentry.api.packetbridge.util.deserializeRoot
import skywolf46.atmospherereentry.api.packetbridge.util.serializeRootTo
import skywolf46.twilightsatellite.common.data.Snapshotable

@NetworkSerializer
class SnapshotSerializer : DataSerializerBase<Snapshotable>(){
    override fun deserialize(buf: ByteBuf): Snapshotable {
        return buf.deserializeRoot()
    }

    override fun serialize(buf: ByteBuf, dataBase: Snapshotable) {
        dataBase.serializeRootTo(buf)
    }
}