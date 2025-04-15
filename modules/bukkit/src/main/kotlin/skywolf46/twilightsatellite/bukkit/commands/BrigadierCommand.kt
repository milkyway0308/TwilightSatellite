package skywolf46.twilightsatellite.bukkit.commands

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommand
import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import org.bukkit.command.CommandSender
import java.util.concurrent.CompletableFuture

class BrigadierCommand(val runner: (CommandArgs<CommandSender>) -> Unit) :
    BukkitBrigadierCommand<BukkitBrigadierCommandSource> {
    override fun run(context: CommandContext<BukkitBrigadierCommandSource>): Int {
        val arg = CommandArgs(
            CommandSender::class.java,
            context.source.bukkitSender,
            context.input.split(" ").toTypedArray(),
            0
        )
        runner(arg)
        return BukkitBrigadierCommand.SINGLE_SUCCESS
    }

    override fun test(t: BukkitBrigadierCommandSource): Boolean {
        return true
    }

    override fun getSuggestions(
        context: CommandContext<BukkitBrigadierCommandSource>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        return Suggestions.empty()
    }

}