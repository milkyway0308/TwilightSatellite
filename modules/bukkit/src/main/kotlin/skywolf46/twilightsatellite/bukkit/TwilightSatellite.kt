package skywolf46.twilightsatellite.bukkit

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.command.UnknownCommandEvent
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import skywolf46.atmospherereentry.api.packetbridge.PacketBridgeClient
import skywolf46.atmospherereentry.api.packetbridge.data.ListenerType
import skywolf46.atmospherereentry.common.api.CoreInitializer
import skywolf46.twilightsatellite.bukkit.configuration.SatelliteConfiguration
import skywolf46.twilightsatellite.bukkit.listener.CommandCompletionListener
import skywolf46.twilightsatellite.bukkit.listener.MotdListener
import skywolf46.twilightsatellite.bukkit.util.TwilightApi
import skywolf46.twilightsatellite.common.annotations.ClientPacketListener
import skywolf46.twilightsatellite.common.annotations.PacketListenerContainer
import java.io.File

class TwilightSatellite : JavaPlugin(), KoinComponent {
    companion object {
        lateinit var instance: TwilightSatellite
            private set

        lateinit var api : TwilightApi
            private set
    }

    lateinit var identifiedName: String
        internal set

    lateinit var config: SatelliteConfiguration
        private set

    lateinit var audience: BukkitAudiences
        private set

    lateinit var client: PacketBridgeClient

    override fun onEnable() {
        instance = this
        Bukkit.getConsoleSender().sendMessage("§eTwilightSatellite §7| §7시작중..")
        checkConfiguration()
        audience = BukkitAudiences.create(this)
        CoreInitializer.init()
        if (isProxyEnabled()) {
            client = PacketBridgeClient.createInstance(config.proxy.host,
                config.proxy.port,
                config.proxy.authKey,
                ListenerType.Reflective(
                    PacketListenerContainer::class.java,
                    ClientPacketListener::class.java
                ) { it.priority })
            Bukkit.getPluginManager().registerEvents(MotdListener(), this)
            Bukkit.getPluginManager().registerEvents(CommandCompletionListener(), this)
            api = TwilightApi(this)
        }
    }

    private fun checkConfiguration() {
        val configDir = File(dataFolder, "config.yml")
        if (!configDir.exists())
            saveResource("config.yml", true)
        this.config = SatelliteConfiguration(YamlConfiguration.loadConfiguration(configDir))
    }

    fun isProxyEnabled(): Boolean {
        return this.config.proxy.enabled
    }
}