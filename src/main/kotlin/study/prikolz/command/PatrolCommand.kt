package study.prikolz.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import study.prikolz.PluginCommands
import study.prikolz.entity.CustomEntities
import study.prikolz.entity.ZombieWardenEntity
import study.prikolz.gui.CustomGUI

object PatrolCommand {

    fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("patrol")
            .then(Commands.literal("add")
                .then(Commands.argument("name", StringArgumentType.greedyString())
                    .executes { context -> runCommand(
                        context.source.sender,
                        StringArgumentType.getString(context, "name"),
                        PatrolAction.ADD
                    )
                    })
            )
            .then(Commands.literal("remove")
                .then(Commands.argument("name", StringArgumentType.greedyString())
                .executes { context -> runCommand(
                    context.source.sender,
                    StringArgumentType.getString(context, "name"),
                    PatrolAction.REMOVE
                )
                }
            ))
            .then(Commands.literal("point")
                .then(Commands.argument("name", StringArgumentType.greedyString())
                .executes { context -> runCommand(
                    context.source.sender,
                    StringArgumentType.getString(context, "name"),
                    PatrolAction.POINT
                    )
                }
            ))
            .build()
    }

    fun runCommand(sender: CommandSender, name: String, action: PatrolAction): Int {
        if (sender !is Player) {
            sender.sendMessage("Command only for players!")
            return 0
        }
        val player: Player = sender

        if (action == PatrolAction.ADD) {
            if (ZombieWardenEntity.patrols.containsKey(name)) {
                player.sendMessage(PluginCommands.getMessage("patrol-add-err").replace("%n", name))
                return 0
            }
            val patrol = ZombieWardenEntity.Patrol(player.uniqueId)
            patrol.points.add(player.location)
            CustomEntities.spawn(ZombieWardenEntity(patrol).also { patrol.wardens.add(it) }, player.location, listOf())
            player.sendMessage(PluginCommands.getMessage("patrol-add").replace("%n", name))
            ZombieWardenEntity.patrols[name] = patrol
            return 1
        }

        val patrol = ZombieWardenEntity.patrols[name]?: run {
            player.sendMessage(PluginCommands.getMessage("patrol-no-exist").replace("%n", name))
            return 0
        }

        if (patrol.owner != player.uniqueId) {
            player.sendMessage(PluginCommands.getMessage("patrol-owner-err").replace("%n", name))
            return 0
        }

        if (action == PatrolAction.REMOVE) {
            patrol.wardens.forEach { it.entity?.remove() }
            ZombieWardenEntity.patrols.remove(name)
            player.sendMessage(PluginCommands.getMessage("patrol-remove").replace("%n", name))
            return 1
        }

        patrol.points.add(player.location)
        player.sendMessage(PluginCommands.getMessage("patrol-point-add").replace("%n", name))
        return 1
    }

    enum class PatrolAction {
        ADD, REMOVE, POINT
    }

}