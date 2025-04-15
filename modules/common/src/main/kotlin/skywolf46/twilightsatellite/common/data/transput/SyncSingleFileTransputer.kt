package skywolf46.twilightsatellite.common.data.transput

import skywolf46.atmospherereentry.api.packetbridge.util.deserializeRoot
import skywolf46.atmospherereentry.api.packetbridge.util.serializeRootToArray
import java.io.File
import java.nio.file.Path


class SyncSingleFileTransputer(private val file: File) : Transputer<Any> {
    @Deprecated("SyncSingleFileTransputer should not be used with key", level = DeprecationLevel.HIDDEN)
    override fun export(key: String, data: Any) {
        if (file.parentFile != null && !file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        if (file.isDirectory) {
            throw IllegalStateException("Target file is directory")
        }
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeBytes(data.serializeRootToArray())
    }

    @Deprecated("SyncSingleFileTransputer should not be used with key", level = DeprecationLevel.HIDDEN)
    override fun import(key: String, data: (Any?) -> Unit) {
        if (!file.exists() || file.isDirectory) {
            data(null)
            return
        }
        data(file.readBytes().deserializeRoot())
    }

    @Deprecated("SyncSingleFileTransputer should not be used with key", level = DeprecationLevel.HIDDEN)
    override fun delete(key: String) {
        file.delete()
    }

    override fun cleanUp(): Boolean {
        // No need to clean up
        return false
    }

    override fun isAsync(): Boolean {
        return false
    }

    fun <T : Any> export(data: T) {
        (this as Transputer<T>).export("", data)
    }

    fun <T : Any> import(): T? {
        if (!file.exists() || file.isDirectory) {
            return null
        }
        return file.readBytes().deserializeRoot()
    }

    fun delete() {
        if (file.exists() && !file.isDirectory) {
            file.delete()
        }
    }
}

fun File.asTransputer(): SyncSingleFileTransputer {
    return SyncSingleFileTransputer(this)
}

fun Path.asTransputer(): SyncSingleFileTransputer {
    return SyncSingleFileTransputer(this.toFile())
}