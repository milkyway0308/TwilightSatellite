package skywolf46.twilightsatellite.bukkit.util

import net.kyori.adventure.audience.Audience
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import skywolf46.twilightsatellite.bukkit.TwilightSatellite

fun <T : Any> Player.adventure(unit: Audience.() -> T): T {
    return unit(TwilightSatellite.instance.audience.player(this))
}

fun <T : Any> CommandSender.adventure(unit: Audience.() -> T): T {
    return unit(this)
}