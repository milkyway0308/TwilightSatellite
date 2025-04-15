package skywolf46.twilightsatellite.velocity

import arrow.core.None
import arrow.core.Option
import arrow.core.toOption
import com.google.common.collect.HashBiMap
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import com.velocitypowered.api.proxy.server.ServerInfo
import com.velocitypowered.api.proxy.server.ServerPing
import net.kyori.adventure.text.TextComponent
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import skywolf46.atmospherereentry.api.packetbridge.PacketBridgeClientConnection
import skywolf46.atmospherereentry.api.packetbridge.PacketBridgeHost
import skywolf46.atmospherereentry.api.packetbridge.data.ListenerType
import skywolf46.atmospherereentry.api.packetbridge.util.JwtProvider
import skywolf46.atmospherereentry.common.api.CoreInitializer
import skywolf46.twilightsatellite.common.annotations.PacketListenerContainer
import skywolf46.twilightsatellite.common.annotations.ProxyPacketListener
import skywolf46.twilightsatellite.common.data.Snapshotable
import skywolf46.twilightsatellite.common.data.codec.DataCodec
import skywolf46.twilightsatellite.common.data.codec.PACKET
import skywolf46.twilightsatellite.common.data.container.FileBaseDataContainer
import skywolf46.twilightsatellite.common.data.transput.Transputer
import skywolf46.twilightsatellite.common.data.transput.file
import skywolf46.twilightsatellite.velocity.data.container.ProxyDataListenerContainer
import skywolf46.twilightsatellite.velocity.util.VelocityPlugin
import skywolf46.twilightsatellite.velocity.util.bindToProxy
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.jvm.optionals.getOrNull


@Plugin(
    id = "twilightsatellite", name = "Twilight Satellight", description = "Orbiting over atmosphere", version = "1.0.0"
)
class TwilightSatellite : VelocityPlugin(), KoinComponent {

    companion object {
        lateinit var instance: TwilightSatellite
            private set
    }

    lateinit var jwtProvider: JwtProvider
        private set

    lateinit var client: PacketBridgeHost
        private set

    private val identifiedServers = HashBiMap.create<ServerInfo, PacketBridgeClientConnection>()

    private val lock = ReentrantReadWriteLock()

    @Subscribe
    override fun onInit(event: ProxyInitializeEvent) {
        instance = this
        logger.info("§eTwilightSatellite §7| §7시작중..")
        CoreInitializer.init()
        jwtProvider = JwtProvider().apply {
            val fileTarget = File(dataDir.toFile(), "satellite.private.key")
            importOrInitializeKeyFrom(fileTarget)
            if (!fileTarget.exists()) {
                exportKeyTo(fileTarget)
                println("§eTwilightSatellite §7| §7새로운 키가 생성되었습니다. 키를 분실하면 기존 서버의 연결 설정을 다시 해야합니다.")
            } else {
                println("§eTwilightSatellite §7| §7키가 로드되었습니다.")
            }
        }
        loadKoinModules(module {
            single { this@TwilightSatellite }
            single { ProxyDataListenerContainer() }
        })

        registerCommands()
        server.scheduler.buildTask(this) {
            client = PacketBridgeHost.createInstance(38810, jwtProvider, ListenerType.Reflective(
                PacketListenerContainer::class.java, ProxyPacketListener::class.java
            ) { it.priority })
            println("§eTwilightSatellite §7| §e패킷 브릿지 호스트가 시작되었습니다.")
        }.delay(1L, TimeUnit.SECONDS).schedule()
    }

    fun findServerFor(info: ServerInfo): Option<PacketBridgeClientConnection> {
        return lock.read {
            identifiedServers[info].toOption()
        }
    }

    fun findServerFor(player: Player): Option<PacketBridgeClientConnection> {
        return findServerFor(player.currentServer.getOrNull()?.serverInfo ?: return None)
    }

    internal fun triggerIdentificationFor(connection: PacketBridgeClientConnection) {
        server.allServers.forEach {
            pingTo(it, connection)
        }
    }

    private fun pingTo(server: RegisteredServer, connection: PacketBridgeClientConnection) {
        server.ping().thenAcceptAsync {
            checkPing(server, it, connection)
        }
    }

    private fun checkPing(server: RegisteredServer, ping: ServerPing, connection: PacketBridgeClientConnection) {
        val rawServerDescription = ping.descriptionComponent
        if (rawServerDescription !is TextComponent) return
        val serverDescription = rawServerDescription.content()
        println("Ping complete! ${server.serverInfo.name} : ${serverDescription}")
        if (!serverDescription.startsWith("Satellite | ")) return
        val serverId = UUID.fromString(serverDescription.substring(12))
        if (serverId == connection.getIdentify()) {
            lock.write {
                identifiedServers[server.serverInfo] = connection
                println("§eTwilightSatellite §7| §7Server ${server.serverInfo.name} identified as $serverId")
            }
        }
    }

    private fun registerCommands() {
        val commandManager = server.commandManager
        commandManager.register(BrigadierCommand(LiteralArgumentBuilder.literal<CommandSource>("satellite")
            .then(LiteralArgumentBuilder.literal<CommandSource?>("list").executes {
                val servers = client.getIdentifiedServers()
                println("- There's [${servers.size}] identified servers.")
                servers.forEach { (k, v) ->
                    println("  - ${k} : ${v.getIdentify()}")
                }
                Command.SINGLE_SUCCESS
            })
            .then(LiteralArgumentBuilder.literal<CommandSource?>("identify")
                .then(
                    RequiredArgumentBuilder.argument<CommandSource?, String?>("serverId", string()).executes {
                        println("Creating JWT signature for server identification...")
                        val signature = jwtProvider.createIdentifier(it.arguments["serverId"]!!.result as String)
                        println("Signature created. Signature :")
                        println(signature)
                        Command.SINGLE_SUCCESS
                    }).executes {
                    println("/satellite identify [Server ID] - Create JWT signature for server identification")
                    Command.SINGLE_SUCCESS
                }).executes {
                println("====================================================")
                println("")
                println("       T W I L I G H T   S A T E L L I T E")
                println("")
                println("====================================================")
                println("/satellite identify [Server ID] - Create JWT signature for server identification")
                println("/satellite list - List all identified servers")
                println("/satellite status - Show status of this server")
                Command.SINGLE_SUCCESS
            }.build()
        )
        )

    }

}