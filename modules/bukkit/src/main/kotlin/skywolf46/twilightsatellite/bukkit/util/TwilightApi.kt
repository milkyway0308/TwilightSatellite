package skywolf46.twilightsatellite.bukkit.util

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import skywolf46.atmospherereentry.api.packetbridge.PacketBase
import skywolf46.atmospherereentry.api.packetbridge.PacketBridgeClient
import skywolf46.atmospherereentry.api.packetbridge.waitReplyOf
import skywolf46.twilightsatellite.bukkit.TwilightSatellite
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass

class TwilightApi(
    val plugin: JavaPlugin, client: () -> PacketBridgeClient? = {
        if (TwilightSatellite.instance.isProxyEnabled()) TwilightSatellite.instance.client else null
    }
) {
    val client by lazy {
        client()
    }

    private val isWarned = AtomicBoolean(false)

    fun sendPacket(packet: PacketBase) {
        _warn()
        client?.send(packet)
    }

    fun <REPLY : PacketBase> waitReply(packet: PacketBase, limitation: KClass<REPLY>, listener: (PacketBase) -> Unit) {
        _warn()
        client?.waitReply(packet, limitation, listener)
    }

    inline fun <reified REPLY : PacketBase> waitReply(packet: PacketBase, noinline listener: (REPLY) -> Unit) {
        _warn()
        client?.waitReplyOf<REPLY>(packet, listener)
    }

    fun <REPLY : PacketBase> waitReplyBukkitSync(
        packet: PacketBase,
        limitation: KClass<REPLY>,
        listener: (REPLY) -> Unit
    ) {
        _warn()
        client?.waitReply(packet, limitation) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin) {
                listener(it)
            }
        }

    }

    inline fun <reified REPLY : PacketBase> waitReplyBukkitSync(
        packet: PacketBase,
        noinline listener: (REPLY) -> Unit
    ) {
        client?.waitReplyOf<REPLY>(packet) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin) {
                listener(it)
            }
        }
    }

    @Suppress("FunctionName")
    @Deprecated("For internal use only - Public for inline methods")
    fun _warn() {
        if (client != null) return
        if (isWarned.compareAndSet(false, true)) {
            plugin.logger.warning("TwilightSatellite Packet API is disabled, but attempted to use it - this may cause unexpected behavior.")
            plugin.logger.warning("This warning will be shown only once.")
        }
    }

}