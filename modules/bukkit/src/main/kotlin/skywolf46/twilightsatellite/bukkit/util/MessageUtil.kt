package skywolf46.twilightsatellite.bukkit.util

import org.bukkit.configuration.file.YamlConfiguration
import skywolf46.twilightsatellite.bukkit.annotations.configuration.MessageConfiguration
import skywolf46.twilightsatellite.bukkit.annotations.configuration.MessageDescription
import skywolf46.twilightsatellite.bukkit.annotations.configuration.MessageMapping
import skywolf46.twilightsatellite.bukkit.data.BukkitMessage
import java.io.File
import java.lang.reflect.Modifier
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.kotlinProperty


object MessageUtil {
    fun <T : Any> parse(cls: Class<T>, file: File): T {
        checkMessageClass(cls)
        if (!file.exists())
            throw IllegalStateException("Cannot parse message configuration from non-exists file")
        try {
            val instance = cls.kotlin.primaryConstructor!!.call()
            val mapped = cls.declaredFields.associateBy {
                it.isAccessible = true
                if (it.getAnnotation(MessageMapping::class.java) != null) {
                    it.getAnnotation(MessageMapping::class.java).messageId
                } else {
                    it.name
                }
            }
            val yaml = YamlConfiguration.loadConfiguration(file)
            mapped.forEach { (it, field) ->
                if (yaml.contains(it)) {
                    when (field.type) {
                        String::class.java -> {
                            field.set(instance, yaml.getString(it) ?: "")
                        }

                        List::class.java -> {
                            field.set(instance, yaml.getStringList(it))
                        }

                        Array<String>::class.java -> {
                            field.set(instance, yaml.getStringList(it).toTypedArray())
                        }

                        BukkitMessage::class.java -> {
                            if (yaml.isList(it)) {
                                field.set(instance, BukkitMessage(*yaml.getStringList(it).toTypedArray()))
                            } else {
                                field.set(instance, BukkitMessage(yaml.getString(it) ?: ""))
                            }
                        }
                    }
                } else {
                    when (field.type) {
                        String::class.java -> {
                            field.set(instance, "")
                        }

                        List::class.java -> {
                            field.set(instance, emptyList<String>())
                        }

                        Array<String>::class.java -> {
                            field.set(instance, emptyArray<String>())
                        }

                        BukkitMessage::class.java -> {
                            field.set(instance, BukkitMessage(""))
                        }
                    }
                }
            }
            return instance
        } catch (e: Throwable) {
            throw IllegalStateException("Cannot parse message configuration from file", e)
        }
    }

    fun saveDefault(cls: Class<*>, file: File) {
        checkMessageClass(cls)
        val yaml = YamlConfiguration()
        val mapped = cls.declaredFields.associateBy {
            it.isAccessible = true
            if (it.getAnnotation(MessageMapping::class.java) != null) {
                it.getAnnotation(MessageMapping::class.java).messageId
            } else {
                it.name
            }
        }
        mapped.forEach { t, u ->
            if (u.kotlinProperty?.isLateinit == false) {
                val path = u.getAnnotation(MessageMapping::class.java)?.messageId ?: u.name
                val data = u.kotlinProperty?.getter?.call(cls.kotlin.primaryConstructor!!.call())
                if (data is BukkitMessage) {
                    yaml[path] = data.message
                } else {
                    yaml[path] =
                        u.get(data ?: return@forEach)
                }
                if (u.getAnnotation(MessageDescription::class.java) != null) {
                    yaml.setComments(path, u.getAnnotation(MessageDescription::class.java).description.toList())
                }
            }
        }
        yaml.save(file)
    }

    fun <T : Any> saveAndLoadMessage(cls: Class<T>, file: File): T {
        checkMessageClass(cls)
        if (!file.exists())
            saveDefault(cls, file)
        return parse(cls, file)
    }

    private fun checkMessageClass(cls: Class<*>) {
        if (cls.getAnnotation(MessageConfiguration::class.java) == null) {
            throw IllegalStateException("Cannot parse message configuration from non-annotated class")
        }
        val primary = cls.kotlin.primaryConstructor ?: throw IllegalStateException("No primary constructor found")
        if (primary.parameters.isNotEmpty())
            throw IllegalStateException("Message class must have no-argument constructor as primary constructor")
        for (x in cls.declaredFields) {
            if (Modifier.isFinal(x.modifiers)) {

                throw IllegalStateException("Cannot parse message configuration from final field. (${cls.name}#${x.name})")
            }
        }
    }
}