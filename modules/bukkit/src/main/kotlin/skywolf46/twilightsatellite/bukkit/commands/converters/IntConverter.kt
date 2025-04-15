package skywolf46.twilightsatellite.bukkit.commands.converters

import org.bukkit.command.CommandSender
import skywolf46.twilightsatellite.bukkit.annotations.commands.AutoArgConverter
import skywolf46.twilightsatellite.bukkit.commands.ArgumentConverter
import skywolf46.twilightsatellite.bukkit.commands.CommandArgs

@AutoArgConverter
class IntConverter : ArgumentConverter<CommandSender, Int> {
    override fun convert(arg: CommandArgs<CommandSender>): Int {
        return arg.next().toInt()
    }

    override fun name(): String {
        return "int"
    }
}