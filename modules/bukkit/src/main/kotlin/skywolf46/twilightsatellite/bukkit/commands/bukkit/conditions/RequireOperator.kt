package skywolf46.twilightsatellite.bukkit.commands.bukkit.conditions

import org.bukkit.command.CommandSender
import skywolf46.twilightsatellite.bukkit.commands.CommandArgs
import skywolf46.twilightsatellite.bukkit.commands.Precondition
import skywolf46.twilightsatellite.bukkit.data.BukkitMessage

fun CommandArgs<CommandSender>.operator(bukkitMessage: BukkitMessage = BukkitMessage("<red>Permission denied.")) =
    RequireOperator(bukkitMessage)

fun CommandArgs<CommandSender>.operator(
    permission: String, bukkitMessage: BukkitMessage = BukkitMessage("<red>Permission denied.")
) = AnyCondition(RequirePermission(permission, bukkitMessage), RequireOperator(bukkitMessage))

class RequireOperator(private val message: BukkitMessage) : Precondition<CommandSender>() {
    override fun check(args: CommandArgs<CommandSender>): Boolean {
        return checkSilent(args).apply {
            if (!this) {
                message.sendTo(args.listener)
            }
        }
    }

    override fun checkSilent(args: CommandArgs<CommandSender>): Boolean {
        return args.listener.isOp
    }
}