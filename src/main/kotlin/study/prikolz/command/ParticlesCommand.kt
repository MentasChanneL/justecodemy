package study.prikolz.command

import com.destroystokyo.paper.ParticleBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import study.prikolz.PluginCommands
import study.prikolz.gui.CustomGUI
import study.prikolz.items.CustomItems
import java.util.*

object ParticlesCommand {

    private val particlesHolders = mutableSetOf<UUID>()
    private val particle = ParticleBuilder(Particle.DRAGON_BREATH).extra(0.0).count(1)

    fun tick() {
        for (uuid in particlesHolders) {
            val player = Bukkit.getPlayer(uuid)?: continue
            val spos = player.eyeLocation.add(0.0, 0.5, 0.0)
            spos.direction = Vector(0, 0, 1)
            for (i in 0..17) {
                spos.yaw = i.toFloat() * 20
                particle.location(spos.clone().add( spos.direction.multiply(0.5) )).spawn()
            }
        }
    }

    fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("particles")
            .then(Commands.literal("show").executes { context -> runCommand(context.source.sender, true) })
            .then(Commands.literal("hide").executes { context -> runCommand(context.source.sender, false) })
            .build()
    }

    fun runCommand(sender: CommandSender, show: Boolean): Int {
        if (sender !is Player) {
            sender.sendMessage("Command only for players!")
            return 0
        }
        val player: Player = sender
        if (show) {
            particlesHolders.add(player.uniqueId)
            sender.sendMessage(PluginCommands.getMessage("particles-show"))
            return 1
        }
        particlesHolders.remove(player.uniqueId)
        sender.sendMessage(PluginCommands.getMessage("particles-hide"))
        return 1
    }

    fun get(holder: UUID): Boolean {
        return particlesHolders.contains(holder)
    }

}