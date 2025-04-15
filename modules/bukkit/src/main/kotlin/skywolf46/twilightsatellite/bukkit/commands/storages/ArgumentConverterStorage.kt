package skywolf46.twilightsatellite.bukkit.commands.storages

import skywolf46.twilightsatellite.bukkit.commands.ArgumentConverter

class ArgumentConverterStorage {
    private val specifiedStorage = mutableMapOf<Class<*>, ArgumentConverterStorage>()
    private val storage = mutableMapOf<Class<*>, ArgumentConverter<*, *>>()

    fun <T : Any> registerConverter(cls: Class<*>, converter: ArgumentConverter<*, T>) {
        storage[cls] = converter
    }

    fun <T : Any> registerConverter(
        audience: Class<*>,
        cls: Class<*>,
        converter: ArgumentConverter<*, T>
    ) {
        specifiedStorage.getOrPut(audience) { ArgumentConverterStorage() }.registerConverter(cls, converter)
    }

    fun getConverter(cls: Class<*>): ArgumentConverter<*, *>? {
        return storage[cls]
    }

    fun <AUDIENCE : Any, T : Any> getConverter(
        audience: Class<AUDIENCE>,
        cls: Class<T>
    ): ArgumentConverter<AUDIENCE, T>? {
        return specifiedStorage[audience]?.getConverter(cls) as? ArgumentConverter<AUDIENCE, T>
    }
}