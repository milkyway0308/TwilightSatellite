package skywolf46.twilightsatellite.bukkit.commands.bukkit.conditions

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import skywolf46.twilightsatellite.bukkit.commands.CommandArgs
import skywolf46.twilightsatellite.bukkit.commands.Precondition
import skywolf46.twilightsatellite.bukkit.data.BukkitMessage
import skywolf46.twilightsatellite.bukkit.util.ifFalse

fun CommandArgs<CommandSender>.player(bukkitMessage: BukkitMessage = BukkitMessage("<red>Player only command.")) =
    PlayerOnly(bukkitMessage)

class PlayerOnly(private val message: BukkitMessage) :
    Precondition<CommandSender>() {
    override fun check(args: CommandArgs<CommandSender>): Boolean {
        return checkSilent(args).ifFalse {
            message.sendTo(args.listener)
        }
    }

    override fun checkSilent(args: CommandArgs<CommandSender>): Boolean {
        return args.listener is Player
    }

}