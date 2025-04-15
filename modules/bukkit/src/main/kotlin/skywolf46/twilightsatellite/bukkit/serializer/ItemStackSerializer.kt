package skywolf46.twilightsatellite.bukkit.serializer

import io.netty.buffer.ByteBuf
import org.bukkit.inventory.ItemStack
import skywolf46.atmospherereentry.api.packetbridge.DataSerializerBase
import skywolf46.atmospherereentry.api.packetbridge.annotations.NetworkSerializer

@NetworkSerializer
class ItemStackSerializer : DataSerializerBase<ItemStack>() {
    override fun deserialize(buf: ByteBuf): ItemStack {
        val array = ByteArray(buf.readInt())
        buf.readBytes(array)
        return ItemStack.deserializeBytes(array)
    }

    override fun serialize(buf: ByteBuf, dataBase: ItemStack) {
        val array = dataBase.serializeAsBytes()
        buf.writeInt(array.size)
        buf.writeBytes(array)
    }

}