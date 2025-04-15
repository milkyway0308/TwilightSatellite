package skywolf46.twilightsatellite.common.yaml

class YamlArray(private val list: List<Any>) {
    fun <T: Any> get(index: Int): T? {
        return list[index] as T?
    }

    fun <T: Any> certainly(index: Int): T {
        return list[index] as T
    }

    fun <T: Any> getArray(index: Int): YamlArray {
        return YamlArray((list[index] as? List<Any>) ?: emptyList())
    }

    fun <T: Any> getMap(index: Int): YamlMap {
        return YamlMap((list[index] as? Map<String, Any>) ?: emptyMap())
    }

    fun size(): Int {
        return list.size
    }
}