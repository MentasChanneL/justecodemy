package study.prikolz.command

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import study.prikolz.PluginCommands.getMessage
import study.prikolz.gui.CustomGUI

object TPSCommand {
    fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("mytps")
            .executes { context -> runCommand(context.source.sender) }
            .build()
    }

    fun runCommand(sender: CommandSender): Int {
        if (!sender.hasPermission("command.mytps")) {
            sender.sendMessage("No permissions!")
            return 0
        }
        sender.sendMessage(tpsComponent())
        return 1
    }

    fun tpsComponent(): Component {
        return Component.text(getMessage("mytps").replace(
            "%f",
            Bukkit.getTPS()[0].toString(),
            false
        ))
    }

}