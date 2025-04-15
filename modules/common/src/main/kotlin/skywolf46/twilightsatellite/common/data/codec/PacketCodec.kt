package skywolf46.twilightsatellite.common.data.codec

import io.netty.buffer.ByteBufAllocator
import skywolf46.atmospherereentry.api.packetbridge.util.deserializeAs
import skywolf46.atmospherereentry.api.packetbridge.util.deserializeRoot
import skywolf46.atmospherereentry.api.packetbridge.util.serializeRootTo
import skywolf46.atmospherereentry.api.packetbridge.util.serializeTo

/**
 * PacketCodec is codec used for packet serialization.
 * Shares serialization logic with AtmosphereReentry Framework.
 */
object PacketCodec : DataCodec {
    override fun serialize(data: Any): ByteArray {
        val buf = ByteBufAllocator.DEFAULT.buffer()
        val cls = data.javaClass.name
        buf.writeByte(cls.length)
        buf.writeCharSequence(cls, Charsets.UTF_8)
        data.serializeTo(buf)
        val arr = ByteArray(buf.readableBytes())
        buf.readBytes(arr)
        buf.release()
        return arr
    }

    override fun deserialize(data: ByteArray): Any {
        val buf = ByteBufAllocator.DEFAULT.buffer()
        buf.writeBytes(data)
        val clsLen = buf.readByte().toInt()
        val cls = Class.forName(buf.readCharSequence(clsLen, Charsets.UTF_8).toString())
        val obj = buf.deserializeAs<Any>(cls)
        buf.release()
        return obj
    }
}

val DataCodec.Companion.PACKET: PacketCodec
    get() = PacketCodec