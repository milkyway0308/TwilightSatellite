package skywolf46.twilightsatellite.common.data.transput

import java.io.File

class SyncFileTransputer(val fileProvider: (String) -> File) : Transputer<ByteArray> {
    override fun export(key: String, data: ByteArray) {
       kotlin.runCatching {
           val file = fileProvider.invoke(key)
           if (file.parentFile != null && !file.parentFile.exists()) {
               file.parentFile.mkdirs()
           }
           if (!file.exists()) {
               file.createNewFile()
           }
           file.writeBytes(data)
       }.onFailure {
           it.printStackTrace()
       }
    }

    override fun import(key: String, data: (ByteArray?) -> Unit) {
        val file = fileProvider.invoke(key)
        if (!file.exists()) {
            data.invoke(null)
            return
        }
        data.invoke(file.readBytes())
    }

    override fun delete(key: String) {
        val file = fileProvider.invoke(key)
        if (file.exists())
            file.delete()
    }

    override fun cleanUp(): Boolean {
        return true
    }

    override fun isAsync(): Boolean {
        return false
    }
}

fun Transputer.Companion.file(isSync: Boolean, fileProvider: (String) -> File): Transputer<ByteArray> {
    return if (isSync)
        SyncFileTransputer(fileProvider)
    else
        TODO()
}