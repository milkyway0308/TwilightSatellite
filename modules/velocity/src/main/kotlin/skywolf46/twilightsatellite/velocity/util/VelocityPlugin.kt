package skywolf46.twilightsatellite.velocity.util

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Path
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject

abstract class VelocityPlugin {

    @Inject
    lateinit var server: ProxyServer
        private set

    @Inject
    protected lateinit var logger: Logger
        private set

    @Inject
    @DataDirectory
    lateinit var dataDir: Path
        private set

    @Subscribe
    open fun onInit(event: ProxyInitializeEvent) {
        // Do nothing, empty for implementation
    }

    @Subscribe
    open fun onDisable(event: ProxyShutdownEvent) {
        // Do nothing, empty for implementation
    }

    fun getResource(fileName: String): InputStream? {
        return kotlin.runCatching {
            val url = javaClass.classLoader.getResource(fileName) ?: return null
            val connection = url.openConnection()
            connection.useCaches = false
            connection.getInputStream()
        }.getOrNull()
    }

    fun saveResource(resourcePath: String, replace: Boolean): Boolean {
        val stream = getResource(resourcePath.replace('\\', '/')) ?: return false
        val outFile = File(dataDir.toFile(), resourcePath)
        val lastIndex = resourcePath.lastIndexOf('/')
        val outDir = File(dataDir.toFile(), resourcePath.substring(0, if (lastIndex >= 0) lastIndex else 0))
        if (!outDir.exists()) {
            outDir.mkdirs()
        }
        if (outFile.exists() && !replace) {
            return false
        }
        kotlin.runCatching {
            FileOutputStream(outFile).use {
                stream.copyTo(it)
            }
            stream.close()
        }.getOrElse {
            logger.log(Level.SEVERE, "Could not save ${outFile.name} to $outFile", it)
            return false
        }
        return true
    }

    fun saveDefaultConfig(replace: Boolean = false) {
        saveResource("config.yml", replace)
    }

}