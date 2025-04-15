package skywolf46.twilightsatellite.bukkit.data

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import skywolf46.twilightsatellite.bukkit.TwilightSatellite

/**
 * Adventure based message holder for Bukkit environment.
 */
class BukkitMessage(vararg val message: String) {
    /**
     * Parse message to Paper Component API.
     *
     * @param resolvers Tag resolvers to resolve tags.
     *   Use [net.kyori.adventure.text.minimessage.tag.resolver.Placeholder] for placeholder resolver.
     * @return Parsed component list.
     */
    fun parse(vararg resolvers: TagResolver): List<Component> {
        if (message.all { it.isEmpty() }) return emptyList()
        return message.map { MiniMessage.miniMessage().deserialize(it, TagResolver.resolver(*resolvers)) }
    }

    /**
     * Send parsed message to sender.
     *
     * @param sender Sender to send message.
     * @param resolvers Tag resolvers to resolve tags.
     *   Use [net.kyori.adventure.text.minimessage.tag.resolver.Placeholder] for placeholder resolver.
     */
    fun sendTo(sender: CommandSender, vararg resolvers: TagResolver) {
        parse(*resolvers).forEach { TwilightSatellite.instance.audience.sender(sender).sendMessage(it) }
    }

    /**
     * Broadcast parsed message to all players and console.
     *
     * @param resolvers Tag resolvers to resolve tags.
     */
    fun broadcast(vararg resolvers: TagResolver) {
        parse(*resolvers).forEach { Bukkit.broadcast(it) }
    }
}