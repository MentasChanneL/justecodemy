package study.prikolz.command

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import study.prikolz.PluginCommands
import study.prikolz.Scores
import java.util.UUID

object ScoreCommand {

    val cooldowns = mutableMapOf<UUID, Int>()

    fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("score")
            .then(Commands.literal("show")
                .executes { context ->
                    if (context.source.sender is Player) {
                        val player = context.source.sender as Player
                        if ((cooldowns[player.uniqueId]?: 0) > Bukkit.getCurrentTick()) {
                            player.sendMessage(PluginCommands.getMessage("score-cd"))
                            return@executes 0
                        }
                        cooldowns[player.uniqueId] = Bukkit.getCurrentTick() + 60
                        Scores.showBoard(player)
                        return@executes 1
                    }
                    context.source.sender.sendMessage("Command only for players!")
                    return@executes 0
                }
            )
            .then(Commands.literal("hide")
                .executes { context ->
                    if (context.source.sender is Player) {
                        val player = context.source.sender as Player
                        if ((cooldowns[player.uniqueId]?: 0) > Bukkit.getCurrentTick()) {
                            player.sendMessage(PluginCommands.getMessage("score-cd"))
                            return@executes 0
                        }
                        cooldowns[player.uniqueId] = Bukkit.getCurrentTick() + 40
                        Scores.hideBoard(player)
                        return@executes 1
                    }
                    context.source.sender.sendMessage("Command only for players!")
                    return@executes 0
                }
            )
            .then(Commands.argument("operation", SuggestionArgumentType(mutableListOf("add", "remove", "set")))
                .then(Commands.argument("player", PlayerArgumentType)
                    .then(Commands.argument("amount", IntegerArgumentType.integer())
                        .executes { context ->
                            runCommand(
                                context.source.sender,
                                StringArgumentType.getString(context, "operation"),
                                PlayerArgumentType.getPlayer(context, "player"),
                                IntegerArgumentType.getInteger(context, "amount")
                            )
                        }
                    )
                )
            )
            .build()
    }

    private fun runCommand(sender: CommandSender, operation: String, player: Player, amount: Int): Int {
        if (!sender.hasPermission("command.score")) {
            sender.sendMessage("No permissions!")
            return 0
        }
        when(operation) {
            "add" -> Scores.addScore(player, amount)
            "remove" -> Scores.addScore(player, -amount)
            "set" -> Scores.setScore(player, amount)
        }
        sender.sendMessage(PluginCommands.getMessage("score-$operation").replace("%p", player.name).replace("%a", amount.toString()))
        return 1
    }
}