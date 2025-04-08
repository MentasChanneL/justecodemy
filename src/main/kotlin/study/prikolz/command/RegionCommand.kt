package study.prikolz.command

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import study.prikolz.Config
import study.prikolz.PluginCommands
import study.prikolz.items.CustomItems

object RegionCommand {
    fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("regionwhitelist")
            .then(Commands.literal("add").then(Commands.argument("player", PlayerArgumentType)
                .executes{ context ->
                    if (!context.source.sender.hasPermission("command.regionwhitelist")) {
                        context.source.sender.sendMessage("No permissions!")
                        return@executes 0
                    }
                    val player = PlayerArgumentType.getPlayer(context, "player")
                    Config.regionWhitelistAdd(player.uniqueId)
                    context.source.sender.sendMessage(PluginCommands.getMessage("regionwhitelist-add").replace("%p", player.name))
                    1
                }
            ))
            .then(Commands.literal("remove").then(Commands.argument("player", PlayerArgumentType)
                .executes{ context ->
                    if (!context.source.sender.hasPermission("command.regionwhitelist")) {
                        context.source.sender.sendMessage("No permissions!")
                        return@executes 0
                    }
                    val player = PlayerArgumentType.getPlayer(context, "player")
                    Config.regionWhitelistRemove(player.uniqueId)
                    context.source.sender.sendMessage(PluginCommands.getMessage("regionwhitelist-remove").replace("%p", player.name))
                    1
                }
            ))
            .build()
    }

}