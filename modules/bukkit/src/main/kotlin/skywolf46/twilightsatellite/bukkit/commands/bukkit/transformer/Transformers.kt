package skywolf46.twilightsatellite.bukkit.commands.bukkit.transformer

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import skywolf46.twilightsatellite.bukkit.commands.CommandArgTransformer
import skywolf46.twilightsatellite.bukkit.data.BukkitMessage

object Transformers {
    @JvmStatic
    @JvmOverloads
    fun player(message: BukkitMessage = BukkitMessage("<red>Player only command.")) : CommandArgTransformer<CommandSender, Player> {
        return PlayerArgTransformer(message)
    }
}