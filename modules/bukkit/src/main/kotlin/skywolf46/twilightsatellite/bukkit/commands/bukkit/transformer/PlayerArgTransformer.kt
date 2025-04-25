package skywolf46.twilightsatellite.bukkit.commands.bukkit.transformer

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import skywolf46.twilightsatellite.bukkit.commands.CommandArgTransformer
import skywolf46.twilightsatellite.bukkit.commands.CommandArgs
import skywolf46.twilightsatellite.bukkit.data.BukkitMessage
import skywolf46.twilightsatellite.bukkit.util.ifNull

fun CommandArgs<CommandSender>.transformPlayer(bukkitMessage: BukkitMessage = BukkitMessage("<red>Player only command.")) =
    PlayerArgTransformer(bukkitMessage)

class PlayerArgTransformer(val onFailed: BukkitMessage) :
    CommandArgTransformer<CommandSender, Player> {
    companion object {
        @JvmStatic
        @JvmOverloads
        @JvmName("transformer")
        fun transformer(bukkitMessage: BukkitMessage = BukkitMessage("<red>Player only command.")) : CommandArgTransformer<CommandSender, Player> {
            return PlayerArgTransformer(bukkitMessage)
        }
    }

    override fun convert(from: CommandArgs<CommandSender>): CommandArgs<Player>? {
        return convertSilent(from).ifNull { onFailed.sendTo(from.listener) }
    }

    override fun convertSilent(from: CommandArgs<CommandSender>): CommandArgs<Player>? {
        if (from.listener !is Player) {
            return null
        }
        return from as CommandArgs<Player>
    }
}