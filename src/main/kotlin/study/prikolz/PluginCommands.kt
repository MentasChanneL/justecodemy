package study.prikolz

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import study.prikolz.command.*
import study.prikolz.gui.CustomGUI
import study.prikolz.gui.InvSeeGUI

object PluginCommands {
    fun register(plugin: Plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS) {
            val r = it.registrar()
            r.register(myTps())
            r.register(reloadConf())
            r.register(invSee())
            r.register(ScoreCommand.build())
            r.register(BowCommand.build())
            r.register(ParticlesCommand.build())
            r.register(RegionCommand.build())
            r.register(PatrolCommand.build())
        }
    }

    private fun myTps(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("mytps").executes { context ->
            if (!context.source.sender.hasPermission("command.mytps")) {
                context.source.sender.sendMessage("No permissions!")
            }else {
                context.source.sender.sendMessage(
                    getMessage("mytps").replace(
                        "%f",
                        Bukkit.getTPS()[0].toString(),
                        false
                    )
                )
            }
            1
        }.build()
    }

    private fun reloadConf(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("reloadconf").executes { context ->
            if (!context.source.sender.hasPermission("command.reloadconf")) {
                context.source.sender.sendMessage("No permissions!")
            }else{
                val errs = Config.readConfig()?: "no"
                context.source.sender.sendMessage("Config reloaded. Errors: $errs")
            }
            1
        }.build()
    }

    private fun invSee(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("invsee").executes { context ->
            invSeeOpenGUI(context, null)
        }.then(Commands.argument("player", PlayerArgumentType).executes { context ->
            invSeeOpenGUI(context, PlayerArgumentType.getPlayer(context, "player"))
        }
        ).build()
    }

    private fun invSeeOpenGUI(context: CommandContext<CommandSourceStack>, player: Player?): Int {
        if (!context.source.sender.hasPermission("command.invsee")) {
            context.source.sender.sendMessage("No permissions!")
            return 0
        }
        if (context.source.sender !is Player) {
            context.source.sender.sendMessage("Only for players!")
            return 0
        }
        val ent: Player = context.source.sender as Player
        val target: Player = player?: ent
        if (!target.isValid) {
            context.source.sender.sendMessage("Player not valid!")
            return 0
        }
        CustomGUI.openGUI(ent, InvSeeGUI(target))
        return 1
    }

    fun getMessage(key: String): String {
        return Config.commandsMessages[key]?: "[MESSAGE NOT FOUND]"
    }
}