package skywolf46.twilightsatellite.bukkit.util

import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

/**
 * Get item count in inventory.
 *
 * @param item item to count
 * @return item count
 */
fun Inventory.getItemCount(item: ItemStack): Int {
    return (0 until maxSize).sumOf {
        val slotItem = getItem(it) ?: return@sumOf 0
        if (slotItem.isSimilar(item)) slotItem.amount else 0
    }
}

/**
 * Check if inventory has certain amount of item.
 *
 * @param item item to check
 * @param count amount to check
 * @return true if inventory has enough item
 */
fun Inventory.hasItemCount(item: ItemStack, count: Int): Boolean {
    return getItemCount(item) >= count
}

/**
 * Remove certain amount of item from inventory.
 *
 * @param item item to remove
 * @param count amount to remove
 * @return true if item removed successfully
 */
fun Inventory.removeItem(item: ItemStack, count: Int): InventoryOperation {
    var remaining = count
    for (x in 0 until maxSize) {
        if (remaining <= 0) return InventoryOperation(count, 0)
        remaining -= removeItemFromSlot(x, item, remaining)
    }
    return InventoryOperation(count - remaining, remaining)
}

/**
 * Remove certain amount of item from inventory.
 *
 * @param item item to remove
 * @param count amount to remove
 * @param preferSlot slot to remove first
 * @return true if item removed successfully
 */
@Suppress("DuplicatedCode")
fun Inventory.removeItemFrom(item: ItemStack, count: Int, preferSlot: Set<Int>): InventoryOperation {
    var remaining = count
    for (x in preferSlot) {
        if (remaining <= 0) return InventoryOperation(count, 0)
        remaining -= removeItemFromSlot(x, item, remaining)
    }
    if (remaining <= 0) return InventoryOperation(count, 0)
    for (x in 0 until maxSize) {
        if (x in preferSlot) continue
        if (remaining <= 0) return InventoryOperation(count, 0)
        remaining -= removeItemFromSlot(x, item, remaining)
    }
    return InventoryOperation(count - remaining, remaining)
}

/**
 * Check if inventory has certain amount of item and remove it.
 *
 * @param item item to check
 * @param count amount to check
 * @return true if inventory has enough item and removed successfully
 */
fun Inventory.checkAndRemoveItem(item: ItemStack, count: Int): Boolean {
    return hasItemCount(item, count) && removeItem(item, count).failed == 0
}


/**
 * Check if inventory has certain amount of item and remove it.
 *
 * @param item item to check
 * @param count amount to check
 * @param preferSlot slot to remove first
 * @return true if inventory has enough item and removed successfully
 */
fun Inventory.checkAndRemoveItem(item: ItemStack, count: Int, preferSlot: Set<Int>): Boolean {
    return hasItemCount(item, count) && removeItemFrom(item, count, preferSlot).failed == 0
}

/**
 * Get empty slot count based on item.
 *
 * @param item item to check
 * @return empty slot count
 */
fun Inventory.getEmptyCountBasedOn(item: ItemStack): Int {
    return (0 until maxSize).sumOf {
        val slotItem = getItem(it) ?: return@sumOf item.type.maxStackSize
        if (slotItem.isSimilar(item)) slotItem.type.maxStackSize - slotItem.amount else 0
    }
}

/**
 * Get empty slot count based on material.
 *
 * @param material material to check
 * @return empty slot count
 */
fun Inventory.getEmptyCountBasedOn(material: Material): Int {
    return (0 until maxSize).sumOf {
        val slotItem = getItem(it) ?: return@sumOf material.maxStackSize
        if (slotItem.type == material) slotItem.type.maxStackSize - slotItem.amount else 0
    }
}

/**
 * Add certain amount of item to inventory, with max stack size limit.
 *
 * @param item item to add
 * @param amount amount to add
 * @return operation result
 */
fun Inventory.addItemNaturally(item: ItemStack, amount: Int): InventoryOperation {
    var remaining = amount
    for (x in 0 until maxSize) {
        if (remaining <= 0) return InventoryOperation(amount, 0)
        remaining -= addItemToSlot(x, item, remaining)
    }
    return InventoryOperation(amount - remaining, remaining)
}

/**
 * Add certain amount of item to inventory.
 * If inventory is full, it will return failed amount.
 *
 * @param slot slot to add item
 * @param item item to add
 * @param count amount to add
 * @return added amount
 */
private fun Inventory.addItemToSlot(slot: Int, item: ItemStack, count: Int): Int {
    val slotItem = getItem(slot)
    if (slotItem == null || slotItem.type == Material.AIR) {
        val clone = item.clone()
        if (count >= clone.type.maxStackSize) {
            clone.amount = clone.type.maxStackSize
            setItem(slot, clone)
            return clone.amount
        } else {
            clone.amount = count
            setItem(slot, clone)
            return count
        }
    } else if (slotItem.isSimilar(item)) {
        val emptyCount = slotItem.type.maxStackSize - slotItem.amount
        if (emptyCount >= count) {
            slotItem.amount += count
            return count
        } else {
            slotItem.amount = slotItem.type.maxStackSize
            return emptyCount
        }
    }
    return 0
}

/**
 * Remove certain amount of item from slot.
 *
 * @param slot slot to remove item
 * @param item item to remove
 * @param count amount to remove
 * @return removed amount
 */
private fun Inventory.removeItemFromSlot(slot: Int, item: ItemStack, count: Int): Int {
    val slotItem = getItem(slot) ?: return 0
    if (slotItem.isSimilar(item)) {
        if (slotItem.amount > count) {
            slotItem.amount -= count
            return count
        } else {
            val removed = slotItem.amount
            setItem(slot, ItemStack(Material.AIR))
            return removed
        }
    }
    return 0
}

val Inventory.maxSize: Int
    get() = if (this is PlayerInventory) 36 else size

data class InventoryOperation(val success: Int, val failed: Int)