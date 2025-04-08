package study.prikolz.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import study.prikolz.PluginCommands
import study.prikolz.entity.CustomEntities
import study.prikolz.entity.ZombieWardenEntity
import study.prikolz.items.CustomItems
import java.util.UUID

object PatrolCommand {

    fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("patrol")
            .then(Commands.literal("add")
                .then(Commands.argument("name", StringArgumentType.greedyString())
                    .executes { context ->
                        if (context.source.sender !is Player) {
                            context.source.sender.sendMessage("Command only for players!")
                            return@executes 0
                        }
                        val player = context.source.sender as Player
                        val name = StringArgumentType.getString(context, "name")
                        if (ZombieWardenEntity.patrols.containsKey(name)) {
                            player.sendMessage(PluginCommands.getMessage("patrol-add-err").replace("%n", name))
                            return@executes 0
                        }
                        val patrol = ZombieWardenEntity.Patrol(player.uniqueId)
                        patrol.points.add(player.location)
                        CustomEntities.spawn(ZombieWardenEntity(patrol).also { patrol.wardens.add(it) }, player.location, listOf())
                        player.sendMessage(PluginCommands.getMessage("patrol-add").replace("%n", name))
                        ZombieWardenEntity.patrols[name] = patrol
                        1
                    })
            )
            .then(Commands.literal("remove").then(Commands.argument("name", StringArgumentType.greedyString())
                .executes { context ->
                    if (context.source.sender !is Player) {
                        context.source.sender.sendMessage("Command only for players!")
                        return@executes 0
                    }
                    val player = context.source.sender as Player
                    val name = StringArgumentType.getString(context, "name")
                    if (!ZombieWardenEntity.patrols.containsKey(name)) {
                        player.sendMessage(PluginCommands.getMessage("patrol-remove-err").replace("%n", name))
                        return@executes 0
                    }
                    val patrol = ZombieWardenEntity.patrols[name]?: return@executes 0
                    patrol.wardens.forEach { it.entity?.remove() }
                    ZombieWardenEntity.patrols.remove(name)
                    player.sendMessage(PluginCommands.getMessage("patrol-remove").replace("%n", name))
                    1
                }
            ))
            .then(Commands.literal("point").then(Commands.argument("name", StringArgumentType.greedyString())
                .executes { context ->
                    if (context.source.sender !is Player) {
                        context.source.sender.sendMessage("Command only for players!")
                        return@executes 0
                    }
                    val player = context.source.sender as Player
                    val name = StringArgumentType.getString(context, "name")
                    if (!ZombieWardenEntity.patrols.containsKey(name)) {
                        player.sendMessage(PluginCommands.getMessage("patrol-point-add-err").replace("%n", name))
                        return@executes 0
                    }
                    val patrol = ZombieWardenEntity.patrols[name]?: return@executes 0
                    patrol.points.add(player.location)
                    player.sendMessage(PluginCommands.getMessage("patrol-point-add").replace("%n", name))
                    1
                }
            ))
            .build()
    }

}