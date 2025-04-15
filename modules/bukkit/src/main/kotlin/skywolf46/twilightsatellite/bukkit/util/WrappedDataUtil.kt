package skywolf46.twilightsatellite.bukkit.util

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import skywolf46.twilightsatellite.common.data.WrappedItemStack
import skywolf46.twilightsatellite.common.data.WrappedLocation


fun WrappedItemStack.unwrap(): ItemStack {
    if (isEmpty()) {
        return ItemStack(Material.AIR)
    }
    return ItemStack.deserializeBytes(this.itemArray)
}

fun ItemStack.wrap(): WrappedItemStack {
    if (type == Material.AIR) {
        return WrappedItemStack.EMPTY
    }
    return WrappedItemStack(serializeAsBytes())
}

fun WrappedLocation.unwrap(): Location {
    return Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
}

fun Location.wrap(): WrappedLocation {
    return WrappedLocation(world!!.name, x, y, z, yaw, pitch)
}