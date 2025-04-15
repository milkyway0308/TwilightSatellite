package skywolf46.twilightsatellite.common.data

import skywolf46.atmospherereentry.api.packetbridge.annotations.ReflectedSerializer

@ReflectedSerializer
class WrappedItemStack(val itemArray: ByteArray) : Snapshotable {
    companion object {
        val EMPTY = WrappedItemStack(ByteArray(0))
    }

    fun isEmpty(): Boolean {
        return itemArray.isEmpty()
    }

    override fun snapshot(): Snapshotable {
        return WrappedItemStack(itemArray)
    }
}