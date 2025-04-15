package skywolf46.twilightsatellite.bukkit.data

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import skywolf46.atmospherereentry.api.packetbridge.util.deserializeFromContext
import skywolf46.atmospherereentry.api.packetbridge.util.serializeAsMap

/**
 * PersistentContainerAccessor supports accessing PersistentDataContainer with more convenient way.
 * With auto-reflected serialization, no more type-unsafe serialization will required.
 *
 * @param plugin Plugin instance for namespace.
 * @param container PersistentDataContainer to access.
 */
class PersistentContainerAccessor(private val plugin: JavaPlugin, private val container: PersistentDataContainer) {
    // ========================================
    //                Getters
    // ========================================

    /**
     * Check if key exists in PersistentDataContainer.
     *
     * @param key Key to check.
     * @return true if key exists.
     */
    fun contains(key: String): Boolean {
        return container.has(NamespacedKey(plugin, key))
    }

    /**
     * Get string value from PersistentDataContainer, or return null if not exists.
     */
    fun getString(key: String): String? {
        return container.get(NamespacedKey(plugin, key), PersistentDataType.STRING)
    }

    /**
     * Get string value from PersistentDataContainer, and throw exception if key is not found.
     */
    fun getStringCertainly(key: String): String {
        return container.get(NamespacedKey(plugin, key), PersistentDataType.STRING)
            ?: throw IllegalStateException("Key $key is not found in PersistentDataContainer.")
    }

    /**
     * Get integer value from PersistentDataContainer, or return null if not exists.
     */
    fun getInt(key: String): Int? {
        return container.get(NamespacedKey(plugin, key), PersistentDataType.INTEGER)
    }

    /**
     * Get integer value from PersistentDataContainer, and throw exception if key is not found.
     */
    fun getIntCertainly(key: String): Int {
        return container.get(NamespacedKey(plugin, key), PersistentDataType.INTEGER)
            ?: throw IllegalStateException("Key $key is not found in PersistentDataContainer.")
    }

    /**
     * Get long value from PersistentDataContainer, or return null if not exists.
     */
    fun getLong(key: String): Long? {
        return container.get(NamespacedKey(plugin, key), PersistentDataType.LONG)
    }

    /**
     * Get long value from PersistentDataContainer, and throw exception if key is not found.
     */
    fun getLongCertainly(key: String): Long {
        return container.get(NamespacedKey(plugin, key), PersistentDataType.LONG)
            ?: throw IllegalStateException("Key $key is not found in PersistentDataContainer.")
    }

    /**
     * Get double value from PersistentDataContainer, or return null if not exists.
     */
    fun getDouble(key: String): Double? {
        return container.get(NamespacedKey(plugin, key), PersistentDataType.DOUBLE)
    }

    /**
     * Get double value from PersistentDataContainer, and throw exception if key is not found.
     */
    fun getDoubleCertainly(key: String): Double {
        return container.get(NamespacedKey(plugin, key), PersistentDataType.DOUBLE)
            ?: throw IllegalStateException("Key $key is not found in PersistentDataContainer.")
    }

    /**
     * Get float value from PersistentDataContainer, or return null if not exists.
     */
    fun getFloat(key: String): Float? {
        return container.get(NamespacedKey(plugin, key), PersistentDataType.FLOAT)
    }

    /**
     * Get float value from PersistentDataContainer, and throw exception if key is not found.
     */
    fun getFloatCertainly(key: String): Float {
        return container.get(NamespacedKey(plugin, key), PersistentDataType.FLOAT)
            ?: throw IllegalStateException("Key $key is not found in PersistentDataContainer.")
    }

    /**
     * Get boolean value from PersistentDataContainer.
     */
    fun getBoolean(key: String): Boolean {
        return container.get(NamespacedKey(plugin, key), PersistentDataType.BYTE) == 1.toByte()
    }

    /**
     * Get boolean value from PersistentDataContainer, or return null if not exists.
     */
    fun getByteArray(key: String): ByteArray? {
        return container.get(NamespacedKey(plugin, key), PersistentDataType.BYTE_ARRAY)
    }

    /**
     * Get boolean value from PersistentDataContainer, and throw exception if key is not found.
     */
    fun getByteArrayCertainly(key: String): ByteArray {
        return container.get(NamespacedKey(plugin, key), PersistentDataType.BYTE_ARRAY)
            ?: throw IllegalStateException("Key $key is not found in PersistentDataContainer.")
    }

    /**
     * Get boolean value from PersistentDataContainer, or return null.
     */
    fun getDataAccessor(key: String): PersistentContainerAccessor? {
        if (!container.has(NamespacedKey(plugin, key), PersistentDataType.TAG_CONTAINER)) return null
        return PersistentContainerAccessor(
            plugin, container.get(NamespacedKey(plugin, key), PersistentDataType.TAG_CONTAINER)!!
        )
    }

    /**
     * Get boolean value from PersistentDataContainer, and throw exception if key is not found.
     */
    fun getDataAccessorCertainly(key: String): PersistentContainerAccessor {
        return PersistentContainerAccessor(
            plugin,
            container.get(NamespacedKey(plugin, key), PersistentDataType.TAG_CONTAINER)
                ?: throw IllegalStateException("Key $key is not found in PersistentDataContainer.")
        )
    }

    /**
     * Get boolean value from PersistentDataContainer, and throw exception if key is not found.
     * Only [skywolf46.atmospherereentry.common.api.packetbridge.ReflectedSerializer] annotated class can be used.
     */
    fun <T : Any> getSerializable(cls: Class<T>, key: String): T? {
        if (!container.has(NamespacedKey(plugin, key), PersistentDataType.TAG_CONTAINER)) return null
        val targetContainer = container.get(NamespacedKey(plugin, key), PersistentDataType.TAG_CONTAINER) ?: return null
        return targetContainer.deserializeFromContext(
            cls,
            { it.keys.map { key -> key.value() } }) { wrappedContainer, wrappedKey, type ->
            when (type) {
                String::class.java -> wrappedContainer.get(NamespacedKey(plugin, wrappedKey), PersistentDataType.STRING)
                Int::class.java -> wrappedContainer.get(NamespacedKey(plugin, wrappedKey), PersistentDataType.INTEGER)
                Long::class.java -> wrappedContainer.get(NamespacedKey(plugin, wrappedKey), PersistentDataType.LONG)
                Double::class.java -> wrappedContainer.get(NamespacedKey(plugin, wrappedKey), PersistentDataType.DOUBLE)
                Float::class.java -> wrappedContainer.get(NamespacedKey(plugin, wrappedKey), PersistentDataType.FLOAT)
                Boolean::class.java -> wrappedContainer.get(
                    NamespacedKey(plugin, wrappedKey), PersistentDataType.BYTE
                ) == 1.toByte()

                ByteArray::class.java -> wrappedContainer.get(
                    NamespacedKey(plugin, wrappedKey), PersistentDataType.BYTE_ARRAY
                )

                HashMap::class.java -> {
                    val subContainer = wrappedContainer.get(
                        NamespacedKey(plugin, wrappedKey), PersistentDataType.TAG_CONTAINER
                    ) ?: return@deserializeFromContext null
                    val isNewFeature = subContainer[NamespacedKey(plugin, "-tw-nf"), PersistentDataType.BOOLEAN] == true
                    val mapType = subContainer[NamespacedKey(plugin, "-type"), PersistentDataType.STRING]
                    if (!isNewFeature && mapType.isNullOrBlank()) {
                        hashMapOf()
                    } else {
                        if (mapType == "empty") {
                            hashMapOf()
                        } else {
                            val deserializer: ((String) -> Any?) = {
                                val targetClass = Class.forName(
                                    mapType ?: subContainer.get(
                                        NamespacedKey(plugin, "-${it}"), PersistentDataType.STRING
                                    )
                                )
                                val data = when (targetClass) {
                                    String::class.java -> subContainer.get(
                                        NamespacedKey(plugin, it), PersistentDataType.STRING
                                    )

                                    Int::class.java, Int::class.javaObjectType -> subContainer.get(
                                        NamespacedKey(
                                            plugin, it
                                        ), PersistentDataType.INTEGER
                                    )

                                    Long::class.java, Long::class.javaObjectType -> subContainer.get(
                                        NamespacedKey(
                                            plugin, it
                                        ), PersistentDataType.LONG
                                    )

                                    Double::class.java, Double::class.javaObjectType -> subContainer.get(
                                        NamespacedKey(
                                            plugin, it
                                        ), PersistentDataType.DOUBLE
                                    )

                                    Float::class.java, Float::class.javaObjectType -> subContainer.get(
                                        NamespacedKey(
                                            plugin, it
                                        ), PersistentDataType.FLOAT
                                    )

                                    Boolean::class.java, Boolean::class.javaObjectType -> subContainer.get(
                                        NamespacedKey(
                                            plugin, it
                                        ), PersistentDataType.BYTE
                                    ) == 1.toByte()

                                    ByteArray::class.java -> subContainer.get(
                                        NamespacedKey(
                                            plugin, it
                                        ), PersistentDataType.BYTE_ARRAY
                                    )

                                    else -> {
                                        throw IllegalArgumentException("Deep map type not supported yet: ${targetClass.name}")
                                    }
                                }
                                data
                            }
                            val map = HashMap<String, Any>()
                            subContainer.keys.forEach {
                                if (it.key == "-tw-nf" || it.key == "-type") return@forEach
                                if (it.key.startsWith('-')) {
                                    val keyName = it.key.substring(1)
                                    map[keyName] = deserializer(keyName)!!
                                }
                            }
                            map
                        }
                    }

                }

                else -> {
                    // We believe normal object serialized as map, so...,
                    wrappedContainer.get(NamespacedKey(plugin, wrappedKey), PersistentDataType.TAG_CONTAINER)
                }
            }
        }
    }

    /**
     * Get value from PersistentDataContainer with generic type.
     *
     * @param key Key to extract.
     * @param cls Class to extract.
     * @return Extracted value.
     */
    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    fun <T : Any> extract(key: String, cls: Class<T>): T? {
        return when (cls) {
            String::class.java -> getString(key)
            Int::class.java -> getInt(key)
            Long::class.java -> getLong(key)
            Double::class.java -> getDouble(key)
            Float::class.java -> getFloat(key)
            Boolean::class.java -> getBoolean(key)
            else -> getSerializable(cls, key)
        } as T?
    }

    fun listKeys(): List<String> {
        return container.keys.map { it.value() }
    }

    // ========================================
    //                Setters
    // ========================================

    /**
     * Set string value to PersistentDataContainer.
     *
     * @param key Key to set.
     * @param value Value to set.
     */
    fun setString(key: String, value: String): PersistentDataContainer {
        container.set(NamespacedKey(plugin, key), PersistentDataType.STRING, value)
        return container
    }

    /**
     * Set integer value to PersistentDataContainer.
     *
     * @param key Key to set.
     * @param value Value to set.
     */
    fun setInt(key: String, value: Int): PersistentDataContainer {
        container.set(NamespacedKey(plugin, key), PersistentDataType.INTEGER, value)
        return container
    }

    /**
     * Set long value to PersistentDataContainer.
     *
     * @param key Key to set.
     * @param value Value to set.
     */
    fun setLong(key: String, value: Long): PersistentDataContainer {
        container.set(NamespacedKey(plugin, key), PersistentDataType.LONG, value)
        return container
    }

    /**
     * Set double value to PersistentDataContainer.
     *
     * @param key Key to set.
     * @param value Value to set.
     */
    fun setDouble(key: String, value: Double): PersistentDataContainer {
        container.set(NamespacedKey(plugin, key), PersistentDataType.DOUBLE, value)
        return container
    }

    /**
     * Set float value to PersistentDataContainer.
     *
     * @param key Key to set.
     * @param value Value to set.
     */
    fun setFloat(key: String, value: Float): PersistentDataContainer {
        container.set(NamespacedKey(plugin, key), PersistentDataType.FLOAT, value)
        return container
    }

    /**
     * Set boolean value to PersistentDataContainer.
     *
     * @param key Key to set.
     * @param value Value to set.
     */
    fun setBoolean(key: String, value: Boolean): PersistentDataContainer {
        container.set(NamespacedKey(plugin, key), PersistentDataType.BYTE, if (value) 1.toByte() else 0.toByte())
        return container
    }

    /**
     * Set byte array to PersistentDataContainer.
     */
    fun setByteArray(key: String, value: ByteArray) {
        container.set(NamespacedKey(plugin, key), PersistentDataType.BYTE_ARRAY, value)
    }

    /**
     * Set PersistentDataContainer to PersistentDataContainer.
     */
    fun setDataAccessor(key: String, value: PersistentContainerAccessor) {
        container.set(NamespacedKey(plugin, key), PersistentDataType.TAG_CONTAINER, value.container)
    }

    /**
     * Set serializable object to PersistentDataContainer.
     *
     * @param key Key to set.
     * @param value Value to set.
     */
    fun putSerializable(key: String, value: Any) {
        val subContainer = container.adapterContext.newPersistentDataContainer()
        putMap(subContainer, value.serializeAsMap())
        container.set(NamespacedKey(plugin, key), PersistentDataType.TAG_CONTAINER, subContainer)
    }

    /**
     * Remove key from PersistentDataContainer.
     */
    fun remove(key: String) {
        container.remove(NamespacedKey(plugin, key))
    }


    // ========================================
    //           Internal Functions
    // ========================================

    /**
     * Put map to PersistentDataContainer.
     *
     * This function is used for internal use only.
     */
    private fun putMap(container: PersistentDataContainer, map: Map<String, Any>) {
        for ((k, v) in map) {
            when (v) {
                is String -> container.set(NamespacedKey(plugin, k), PersistentDataType.STRING, v)
                is Int -> container.set(NamespacedKey(plugin, k), PersistentDataType.INTEGER, v)
                is Long -> container.set(NamespacedKey(plugin, k), PersistentDataType.LONG, v)
                is Double -> container.set(NamespacedKey(plugin, k), PersistentDataType.DOUBLE, v)
                is Float -> container.set(NamespacedKey(plugin, k), PersistentDataType.FLOAT, v)
                is Boolean -> container.set(
                    NamespacedKey(plugin, k), PersistentDataType.BYTE, if (v) 1.toByte() else 0.toByte()
                )

                is ByteArray -> container.set(NamespacedKey(plugin, k), PersistentDataType.BYTE_ARRAY, v)

                is Map<*, *> -> {
                    val subContainer = container.adapterContext.newPersistentDataContainer()
                    putMap(subContainer, v as Map<String, Any>)
//                    subContainer[NamespacedKey(plugin, "-type"), PersistentDataType.STRING] =
//                        if (v.isEmpty()) "" else v.values.first().javaClass.name
                    subContainer[NamespacedKey(plugin, "-tw-nf"), PersistentDataType.BOOLEAN] = true
                    container.set(NamespacedKey(plugin, k), PersistentDataType.TAG_CONTAINER, subContainer)
                }

                else -> throw IllegalArgumentException("Unsupported type: ${v::class.java.name}")
            }
            container.set(NamespacedKey(plugin, "-${k}"), PersistentDataType.STRING, v.javaClass.name)
        }
    }

    // ========================================
    //           Inline Functions
    // ========================================

    /**
     * Get serialized object from PersistentDataContainer.
     */
    inline fun <reified T : Any> getSerializable(key: String): T? {
        return getSerializable(T::class.java, key)
    }

    /**
     * Extract value from PersistentDataContainer with generic type.
     * @generic T Type to extract.
     * @param key Key to extract.
     * @return Extracted value.
     */
    inline fun <reified T : Any> extract(key: String): T? {
        return extract(key, T::class.java)
    }


    // ========================================
    //           Utility Functions
    // ========================================
    fun newAccessor(): PersistentContainerAccessor {
        return PersistentContainerAccessor(plugin, container.adapterContext.newPersistentDataContainer())
    }
}