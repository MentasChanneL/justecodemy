package study.prikolz.command

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import org.bukkit.block.BlockState
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import study.prikolz.PluginCommands
import study.prikolz.Selection
import study.prikolz.gui.CustomGUI
import study.prikolz.gui.InfoGUI

object SetCommand {
    fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("#set")
            .then(Commands.argument("block", ArgumentTypes.blockState())
                .executes { context -> runCommand(
                    context.source.sender,
                    context.getArgument("block", BlockState::class.java)
                ) }
            )
            .build()
    }

    fun runCommand(sender: CommandSender, blockState: BlockState): Int {
        if (sender !is Player) {
            sender.sendMessage("Command only for players!")
            return 0
        }
        val player: Player = sender
        if (!sender.hasPermission("command.set")) {
            sender.sendMessage("No permissions!")
            return 0
        }
        val selection = Selection.selections[player]?: run {
            sender.sendMessage(PluginCommands.getMessage("region-no-selection"))
            return 0
        }

        selection.forEachRegion(condition = { _, _, _ -> Selection.BlockPlacement(blockState, true)})

        return 1
    }

}