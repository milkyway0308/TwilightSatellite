package skywolf46.twilightsatellite.bukkit.commands.bukkit.arguments

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class PlayerArgumentType : ArgumentType<Player> {
    override fun parse(reader: StringReader): Player? {
        return Bukkit.getPlayer(reader.readString())
    }

    override fun <S : Any?> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        Bukkit.getOnlinePlayers().filter { it.name.lowercase().startsWith(builder.remaining.lowercase()) }.map {
            builder.suggest(it.name)
        }
        return CompletableFuture.completedFuture(builder.build())
    }

}