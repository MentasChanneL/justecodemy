package study.prikolz.command

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import study.prikolz.gui.CustomGUI
import study.prikolz.items.CustomItems

object BowCommand {
    fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("bow")
            .then(Commands.literal("tnt").executes { context ->
                if (context.source.sender is Player) {
                    val player = context.source.sender as Player
                    runCommand(player, true)
                    return@executes 1
                }
                context.source.sender.sendMessage("Command only for players!")
                0
            }
            )
            .executes { context ->
                if (context.source.sender is Player) {
                    val player = context.source.sender as Player
                    runCommand(player, false)
                    return@executes 1
                }
                context.source.sender.sendMessage("Command only for players!")
                0
            }
            .build()
    }

    fun runCommand(player: Player, explosive: Boolean?) {
        if (explosive != false) CustomItems.get("ultimate_breaker_bow")?.also { player.inventory.addItem(it) }
        if (explosive != true) CustomItems.get("breaker_bow")?.also { player.inventory.addItem(it) }
        player.inventory.addItem(ItemStack(Material.ARROW, 50))
    }

}