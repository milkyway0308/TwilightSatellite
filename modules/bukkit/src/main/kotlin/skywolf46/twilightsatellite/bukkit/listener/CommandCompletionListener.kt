package skywolf46.twilightsatellite.bukkit.listener

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import skywolf46.twilightsatellite.bukkit.event.CommandSuggestionEvent

class CommandCompletionListener : Listener {
    @EventHandler
    fun onCommandCompletion(event: CommandSuggestionEvent) {
        if (event.commandOwner == Any::class) {
            if (event.matcher.getCommandName() == "player") {
                val args = event.builder.input.split(" ")
                val lastArg = args.last()
                if (lastArg.isEmpty()) {
                    Bukkit.getOnlinePlayers().forEach {
                        event.builder.suggest(it.name)
                    }
                } else {
                    Bukkit.getOnlinePlayers().filter {
                        it.name.startsWith(lastArg)
                    }.forEach {
                        event.builder.suggest(it.name)
                    }
                }
            }
        }
    }
}