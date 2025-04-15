package skywolf46.twilightsatellite.bukkit.serializer

import io.netty.buffer.ByteBuf
import org.bukkit.Bukkit
import org.bukkit.Location
import skywolf46.atmospherereentry.api.packetbridge.DataSerializerBase
import skywolf46.atmospherereentry.api.packetbridge.annotations.NetworkSerializer
import skywolf46.atmospherereentry.api.packetbridge.util.readString
import skywolf46.atmospherereentry.api.packetbridge.util.writeString

@NetworkSerializer
class LocationSerializer : DataSerializerBase<Location>() {
    override fun deserialize(buf: ByteBuf): Location {
        return Location(
            Bukkit.getWorld(buf.readString())!!,
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble()
        )
    }

    override fun serialize(buf: ByteBuf, dataBase: Location) {
        buf.writeString(dataBase.world!!.name)
        buf.writeDouble(dataBase.x)
        buf.writeDouble(dataBase.y)
        buf.writeDouble(dataBase.z)
    }
}