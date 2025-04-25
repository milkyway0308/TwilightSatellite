package skywolf46.twilightsatellite.bukkit.util

import net.kyori.adventure.audience.Audience
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import skywolf46.twilightsatellite.bukkit.TwilightSatellite

object AdventureUtil {

    @JvmStatic
    @JvmName("adventure")
    fun adventure(player: Player): Audience {
        return TwilightSatellite.instance.audience.player(player)
    }

    @JvmStatic
    @JvmName("adventureSender")
    fun adventureSender(sender: CommandSender): Audience {
        return TwilightSatellite.instance.audience.sender(sender)
    }
}

fun <T : Any> Player.adventure(unit: Audience.() -> T): T {
    return unit(TwilightSatellite.instance.audience.player(this))
}

fun <T : Any> CommandSender.adventure(unit: Audience.() -> T): T {
    return unit(this)
}