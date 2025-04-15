package skywolf46.twilightsatellite.common.data.codec

interface DataCodec {
    companion object

    fun serialize(data: Any): ByteArray

    fun deserialize(data: ByteArray): Any
}