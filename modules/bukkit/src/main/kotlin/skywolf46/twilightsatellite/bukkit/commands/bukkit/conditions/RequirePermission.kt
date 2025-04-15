package skywolf46.twilightsatellite.bukkit.commands.bukkit.conditions

import org.bukkit.command.CommandSender
import skywolf46.twilightsatellite.bukkit.commands.CommandArgs
import skywolf46.twilightsatellite.bukkit.commands.Precondition
import skywolf46.twilightsatellite.bukkit.data.BukkitMessage

fun CommandArgs<CommandSender>.permission(
    permission: String, bukkitMessage: BukkitMessage = BukkitMessage("<red>Permission denied.")
) = RequirePermission(permission, bukkitMessage)

class RequirePermission(private val permission: String, private val message: BukkitMessage) :
    Precondition<CommandSender>() {
    override fun check(args: CommandArgs<CommandSender>): Boolean {
        return checkSilent(args).apply {
            if (!this) {
                message.sendTo(args.listener)
            }
        }
    }

    override fun checkSilent(args: CommandArgs<CommandSender>): Boolean {
        return args.listener.hasPermission(permission)
    }
}