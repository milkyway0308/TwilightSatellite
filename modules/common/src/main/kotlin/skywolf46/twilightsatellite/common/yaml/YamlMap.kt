package skywolf46.twilightsatellite.common.yaml

import org.yaml.snakeyaml.Yaml
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class YamlMap(private val map: Map<String, Any>) {
    companion object {
        private val yaml = Yaml()

        fun fromStream(inputStream: InputStream, encoding: Charset = StandardCharsets.UTF_8): YamlMap {
            return inputStream.reader(encoding).use {
                YamlMap(yaml.load(it) as Map<String, Any>)
            }
        }
    }

    fun <T : Any> get(key: String): T? {
        return map[key] as T?
    }

    fun <T : Any> certainly(key: String): T {
        return map[key] as T
    }

    fun getMap(key: String): YamlMap {
        return YamlMap((map[key] as? Map<String, Any>) ?: emptyMap())
    }

    fun getArray(key: String): YamlArray {
        return YamlArray((map[key] as? List<Any>) ?: emptyList())
    }

    fun getKeys(): Set<String> {
        return map.keys
    }
}