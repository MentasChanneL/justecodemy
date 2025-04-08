package study.prikolz.command

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import study.prikolz.gui.CustomGUI
import study.prikolz.gui.InfoGUI

object InfoCommand {
    fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("info")
            .executes { context -> runCommand(context.source.sender)}
            .build()
    }

    fun runCommand(sender: CommandSender): Int {
        if (sender !is Player) {
            sender.sendMessage("Command only for players!")
            return 0
        }
        val player: Player = sender
        if (!sender.hasPermission("command.info")) {
            sender.sendMessage("No permissions!")
            return 0
        }
        CustomGUI.openGUI(player, InfoGUI(player))
        return 1
    }

}