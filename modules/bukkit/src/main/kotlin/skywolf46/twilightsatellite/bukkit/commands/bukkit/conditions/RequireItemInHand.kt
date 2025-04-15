package skywolf46.twilightsatellite.bukkit.commands.bukkit.conditions

import org.bukkit.Material
import org.bukkit.entity.Player
import skywolf46.twilightsatellite.bukkit.commands.CommandArgs
import skywolf46.twilightsatellite.bukkit.commands.Precondition
import skywolf46.twilightsatellite.bukkit.data.BukkitMessage
import skywolf46.twilightsatellite.bukkit.util.ifFalse

fun CommandArgs<Player>.requireItemInHand(
    message: BukkitMessage = BukkitMessage(
        "<red>This command requires item in hand.</red>"
    )
) = RequireItemInHand(message)

class RequireItemInHand(private val message: BukkitMessage) : Precondition<Player>() {
    override fun check(args: CommandArgs<Player>): Boolean {
        return checkSilent(args).ifFalse {
            message.sendTo(args.listener)
        }
    }

    override fun checkSilent(args: CommandArgs<Player>): Boolean {
        return args.listener.inventory.itemInMainHand.type != Material.AIR
    }
}