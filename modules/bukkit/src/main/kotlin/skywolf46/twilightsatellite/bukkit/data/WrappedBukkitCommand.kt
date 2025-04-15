package skywolf46.twilightsatellite.bukkit.data

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import org.bukkit.command.CommandSender
import skywolf46.twilightsatellite.bukkit.commands.CommandArgs

class WrappedBukkitCommand(val builder: ArgumentBuilder<Any, *>) {
    companion object {
        private val commandSenderExtractor =
            Class.forName("net.minecraft.commands.CommandListenerWrapper").getMethod("getBukkitSender")
    }

    private var isBound = false

    fun requires(sender: (CommandSender) -> Boolean): WrappedBukkitCommand {
        builder.requires { sender.invoke(it.extractCommandSender()) }
        return this
    }

    fun executes(unit: (CommandArgs<CommandSender>) -> Unit): WrappedBukkitCommand {
        if (isBound) return this
        isBound = true
        builder.executes {
            kotlin.runCatching {
                unit.invoke(CommandArgs(it.source.extractCommandSender(), it.input, 0))
            }.onFailure { it.printStackTrace() }
            Command.SINGLE_SUCCESS
        }
        return this
    }

    fun then(subCommand: WrappedBukkitCommand): WrappedBukkitCommand {
        builder.then(subCommand.builder)
        return this
    }

    fun Any.extractCommandSender(): CommandSender {
        return commandSenderExtractor.invoke(this) as CommandSender
    }

    fun literal(): LiteralArgumentBuilder<Any> {
        return builder as LiteralArgumentBuilder<Any>
    }
}