package study.prikolz.command

import com.destroystokyo.paper.ParticleBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import study.prikolz.PluginCommands
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
            .then(Commands.literal("show").executes { context ->
                if (context.source.sender !is Player) {
                    context.source.sender.sendMessage("Command only for players!")
                    return@executes 0
                }
                val player = context.source.sender as Player
                particlesHolders.add(player.uniqueId)
                player.sendMessage(PluginCommands.getMessage("particles-show"))
                1
            })
            .then(Commands.literal("hide").executes { context ->
                if (context.source.sender !is Player) {
                    context.source.sender.sendMessage("Command only for players!")
                    return@executes 0
                }
                val player = context.source.sender as Player
                particlesHolders.remove(player.uniqueId)
                player.sendMessage(PluginCommands.getMessage("particles-hide"))
                1
            })
            .build()
    }

}