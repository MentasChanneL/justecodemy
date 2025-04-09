package study.prikolz.command

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import org.bukkit.Location
import org.bukkit.block.BlockState
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import study.prikolz.Selection
import kotlin.math.*

object SphereCommand {
    fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("#sphere")
            .then(Commands.argument("block", ArgumentTypes.blockState())
                .then(Commands.argument("radius", IntegerArgumentType.integer(0))
                    .executes { context -> runCommand(
                        context.source.sender,
                        context.getArgument("block", BlockState::class.java),
                        IntegerArgumentType.getInteger(context, "radius")
                    ) }
                )
            )
            .build()
    }

    fun runCommand(sender: CommandSender, blockState: BlockState, radius: Int): Int {
        if (sender !is Player) {
            sender.sendMessage("Command only for players!")
            return 0
        }
        val player: Player = sender
        if (!sender.hasPermission("command.sphere")) {
            sender.sendMessage("No permissions!")
            return 0
        }
        val selection = Selection.selections[player]?: Selection(
            player.location.toBlockLocation().add(radius.toDouble(), radius.toDouble(), radius.toDouble()),
            player.location.toBlockLocation().add(radius * -1.0, radius * -1.0, radius * -1.0)
        )
        selection.forEachRegion(condition = { x, y, z -> sphereCondition(x, y, z, player.location.toBlockLocation(), blockState, radius)})
        return 1
    }

    fun sphereCondition(x: Int, y: Int, z: Int, center: Location, blockState: BlockState, radius: Int): Selection.BlockPlacement {
        val result = Selection.BlockPlacement(blockState, false)
        val dist = sqrt( (x.toDouble() - center.blockX).pow(2.0) + (y.toDouble() - center.blockY).pow(2.0) + (z.toDouble() - center.blockZ).pow(2.0) )
        if (dist < radius) result.place = true
        return result
    }

}