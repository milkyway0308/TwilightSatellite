package skywolf46.twilightsatellite.bukkit.util

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.plugin.java.JavaPlugin
import skywolf46.twilightsatellite.bukkit.data.PersistentContainerAccessor

// ========================================
//           ItemMeta Wrapper
// ========================================
/**
 * Modify ItemMeta of ItemStack.
 *
 * This function not handle exception tho, so it will throw exception if error occurred.
 *
 * @param unit lambda to modify ItemMeta
 * @return result of lambda or error if occurred
 */
inline fun ItemStack.meta(unit: ItemMeta.() -> Unit) {
    val meta = itemMeta ?: return
    unit(meta)
    itemMeta = meta
}

/**
 * Modify ItemMeta of ItemStack, and return result of lambda or error.
 *
 * @param unit lambda to modify ItemMeta
 * @return result of lambda or error if occurred
 */
inline fun <T : Any> ItemStack.mapMeta(unit: ItemMeta.() -> T): Either<Throwable, T> {
    val meta = itemMeta ?: return IllegalStateException("No item meta found").left()
    val result = runCatching {
        unit(meta)
    }.getOrElse {
        return it.left()
    }
    itemMeta = meta
    return result.right()
}

/**
 * Modify ItemMeta of ItemStack.
 *
 * This function not handle exception tho, so it will throw exception if error occurred.
 *
 * @param unit lambda to modify ItemMeta
 * @return ItemStack
 */
inline fun ItemStack.setupMeta(unit: ItemMeta.() -> Unit): ItemStack {
    val meta = itemMeta ?: return this
    unit(meta)
    itemMeta = meta
    return this
}

/**
 * Observe ItemMeta of ItemStack.
 *
 * This function do not modify ItemMeta.
 * This function not handle exception tho, so it will throw exception if error occurred.
 *
 * @param unit lambda to modify ItemMeta
 */
inline fun ItemStack.observeMeta(unit: ItemMeta.() -> Unit) {
    unit(itemMeta ?: return)
}

/**
 * Observe ItemMeta of ItemStack, and return result of lambda or error.
 *
 * This function do not modify ItemMeta.
 *
 * @param unit lambda to modify ItemMeta
 * @return Result of lambda or error if occurred
 */
inline fun <T : Any> ItemStack.mapObserveMeta(unit: ItemMeta.() -> T): Either<Throwable, T> {
    val meta = itemMeta ?: return IllegalStateException("No item meta found").left()
    return kotlin.runCatching {
        unit(meta).right()
    }.getOrElse {
        it.left()
    }
}

/**
 * Cast ItemMeta to specific type, and modify it.
 *
 * This function not occur exception if ItemMeta is not specific type, so have to check type before modify.
 *
 * @param unit lambda to cast and modify ItemMeta
 */
inline fun <reified T : ItemMeta> ItemStack.castMeta(unit: T.() -> Unit) {
    meta {
        if (this is T) {
            unit.invoke(this)
        }
    }
}

/**
 * Cast ItemMeta to specific type, and return result of lambda or error.
 *
 * @param unit lambda to cast and modify ItemMeta
 * @return Result of lambda or error if occurred
 */
inline fun <reified T : ItemMeta, X : Any> ItemStack.mapCastMeta(unit: T.() -> X): Either<Throwable, X> {
    return mapMeta {
        if (this is T) {
            unit.invoke(this)
        } else {
            return IllegalStateException("ItemMeta is not ${T::class.simpleName}").left()
        }
    }
}

/**
 * Cast ItemMeta to specific type.
 *
 * This function not handle exception tho, so it will throw exception if error occurred.
 * This function not occur exception if ItemMeta is not specific type.
 *
 * @param unit lambda to cast and modify ItemMeta
 * @return ItemStack itself
 */
inline fun <reified T : ItemMeta> ItemStack.setupCastMeta(unit: T.() -> Unit): ItemStack {
    castMeta<T>(unit)
    return this
}

/**
 * Observe ItemMeta of ItemStack.
 *
 * This function not handle exception tho, so it will throw exception if error occurred.
 *
 * @param unit lambda to modify ItemMeta
 */
inline fun <reified T : ItemMeta> ItemStack.observeCastMeta(unit: T.() -> Unit) {
    observeMeta {
        if (this is T) {
            unit.invoke(this)
        }
    }
}

/**
 * Observe ItemMeta of ItemStack, and return result of lambda or error.
 *
 * @param unit lambda to modify ItemMeta
 * @return Result of lambda or error if occurred
 */
inline fun <reified T : ItemMeta, X : Any> ItemStack.mapObserveCastMeta(unit: T.() -> X): Either<Throwable, X> {
    return mapObserveMeta {
        if (this is T) {
            unit.invoke(this)
        } else {
            throw IllegalStateException("ItemMeta is not ${T::class.simpleName}")
        }
    }
}


// ========================================
//     Persistent Data Container Related
//             (1.19+ Only)
// ========================================

/**
 * Access to persistent data container of ItemMeta.
 *
 * @param unit lambda to access PersistentDataContainer
 */
inline fun ItemMeta.persistent(unit: PersistentDataContainer.() -> Unit) {
    unit(persistentDataContainer)
}

/**
 * Access to persistent data container of ItemMeta, and return result of lambda.
 *
 * @param unit lambda to access PersistentDataContainer
 * @return result of lambda
 */
inline fun <T : Any> ItemMeta.mapPersistent(unit: PersistentDataContainer.() -> T): T {
    return unit(persistentDataContainer)
}

/**
 * Access and manage persistent data container of ItemMeta.
 * PersistentDataAccessor has ability to serialize, or deserialize data through reflection.
 *
 * @param plugin JavaPlugin instance
 * @param unit lambda to access PersistentDataContainer
 */
inline fun ItemMeta.persistentAccess(plugin: JavaPlugin, unit: PersistentContainerAccessor.() -> Unit) {
    unit(PersistentContainerAccessor(plugin, persistentDataContainer))
}

/**
 * Access to persistent data container of ItemMeta.
 * This function do not modify PersistentDataContainer.
 *
 * @param unit lambda to access PersistentDataContainer
 */
inline fun ItemMeta.observePersistent(unit: PersistentDataContainer.() -> Unit) {
    unit(persistentDataContainer)
}

/**
 * Access and manage persistent data container of ItemMeta, and return result of lambda.
 * PersistentDataAccessor has ability to serialize, or deserialize data through reflection.
 *
 * @param plugin JavaPlugin instance
 * @param unit lambda to access PersistentDataContainer
 */
inline fun <T : Any> ItemMeta.mapPersistentAccess(plugin: JavaPlugin, unit: PersistentContainerAccessor.() -> T): T {
    return unit(PersistentContainerAccessor(plugin, persistentDataContainer))
}

/**
 * Access to persistent data container of ItemStack.
 *
 * @param unit lambda to access PersistentDataContainer
 */
inline fun ItemStack.observePersistent(unit: PersistentDataContainer.() -> Unit) {
    itemMeta?.persistent(unit)
}

/**
 * Access to persistent data container of ItemStack, and return result of lambda.
 * This function do not modify PersistentDataContainer.
 *
 * @param unit lambda to access PersistentDataContainer
 * @return result of lambda
 */
inline fun <T : Any> ItemStack.mapObservePersistent(unit: PersistentDataContainer.() -> T): T {
    return itemMeta?.mapPersistent(unit) ?: throw IllegalStateException("No item meta found")
}

/**
 * Access and manage persistent data container of ItemStack.
 * PersistentDataAccessor has ability to serialize, or deserialize data through reflection.
 *
 * @param plugin JavaPlugin instance
 * @param unit lambda to access PersistentDataContainer
 * @return Persistent access success or not
 */
inline fun ItemStack.observerPersistentAccess(
    plugin: JavaPlugin, unit: PersistentContainerAccessor.() -> Unit
): Boolean {
    itemMeta?.persistentAccess(plugin, unit) ?: return false
    return true
}

/**
 * Access and manage persistent data container of ItemStack, and return result of lambda.
 * PersistentDataAccessor has ability to serialize, or deserialize data through reflection.
 *
 * @param plugin JavaPlugin instance
 * @param unit lambda to access PersistentDataContainer
 * @return result of lambda
 */
inline fun <T : Any> ItemStack.mapPersistentAccess(plugin: JavaPlugin, unit: PersistentContainerAccessor.() -> T): T {
    return itemMeta?.mapPersistentAccess(plugin, unit) ?: throw IllegalStateException("No item meta found")
}

// ========================================
//           ItemMeta Related
// ========================================

/**
 * Modify display name of item.
 *
 * @param name display name
 * @return ItemMeta itself
 */
fun ItemMeta.updateDisplayName(name: String): ItemMeta {
    setDisplayName(name)
    return this
}

/**
 * Modify display name of item.
 *
 * @param name display name
 * @return ItemMeta itself
 */
fun ItemMeta.updateDisplayName(name: Component): ItemMeta {
    displayName(name)
    return this
}

/**
 * Modify lore of item.
 *
 * @param lore lore
 * @return ItemMeta itself
 */
fun ItemMeta.updateLore(lore: List<String>): ItemMeta {
    setLore(lore)
    return this
}

/**
 * Modify lore of item.
 *
 * @param lore lore
 * @return ItemMeta itself
 */
fun ItemMeta.updateLore(vararg lore: Component): ItemMeta {
    lore(lore.toList())
    return this
}

// ========================================
//           ItemStack Related
// ========================================

/**
 * Modify amount of item.
 *
 * @param amount amount
 * @return ItemStack itself
 */
fun ItemStack.amount(amount: Int): ItemStack {
    this.amount = amount
    return this
}

/**
 * Modify durability of item.
 * If item is not damageable, this function do nothing.
 *
 * @param damage durability
 * @return ItemStack itself
 */
fun ItemStack.damage(damage: Int): ItemStack {
    castMeta<Damageable> {
        this.damage = damage
    }
    return this
}

/**
 * Dye leather armour.
 *
 * @param color RGB color code
 * @return ItemStack itself
 */
fun ItemStack.dyeArmour(color: Int): ItemStack {
    castMeta<LeatherArmorMeta> {
        setColor(Color.fromRGB(color))
    }
    return this
}

/**
 * Dye leather armour.
 *
 * @param colorCode RGB color code
 * @return ItemStack itself
 */
fun ItemStack.dyeArmour(colorCode: String): ItemStack {
    castMeta<LeatherArmorMeta> {
        if (colorCode.startsWith('#')) setColor(Color.fromRGB(colorCode.substring(1).toInt(16)))
        else setColor(Color.fromRGB(colorCode.toInt(16)))
    }
    return this
}

/**
 * Dye leather armour.
 *
 * @param r red
 * @param g green
 * @param b blue
 * @return ItemStack itself
 */
fun ItemStack.dyeArmour(r: Int, g: Int, b: Int): ItemStack {
    castMeta<LeatherArmorMeta> {
        setColor(Color.fromRGB(r, g, b))
    }
    return this
}


/**
 * Acquire name of item.
 *
 * @return name of item
 */
fun ItemStack.acquireName() : String {
    return itemMeta?.displayName ?: type.name
}