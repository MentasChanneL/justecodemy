package study.prikolz.command

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import study.prikolz.gui.CustomGUI
import study.prikolz.gui.InfoGUI
import study.prikolz.items.CustomItems

object WandCommand {
    fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("#wand")
            .executes { context -> runCommand(context.source.sender)}
            .build()
    }

    fun runCommand(sender: CommandSender): Int {
        if (sender !is Player) {
            sender.sendMessage("Command only for players!")
            return 0
        }
        val player: Player = sender
        CustomItems.get("wand")?.also { player.inventory.addItem(it) }
        return 1
    }

}