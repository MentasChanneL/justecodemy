package study.prikolz.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapPalette
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import study.prikolz.Plugin
import study.prikolz.PluginCommands
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO

object ImageURLCommand {

    var activeRequests = 0

    fun build(plugin: Plugin): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("image")
            .then(Commands.argument("url", StringArgumentType.greedyString())
                .executes { context ->
                    if (activeRequests > 4) {
                        context.source.sender.sendMessage(PluginCommands.getMessage("image-many-requests"))
                        return@executes 0
                    }
                    val url = StringArgumentType.getString(context, "url")
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                        activeRequests++
                        try {
                            var image = ImageIO.read(URL(url))
                            image = MapPalette.resizeImage(image)
                            val view = Bukkit.createMap(Bukkit.getWorlds().first())
                            view.renderers.clear()
                            view.addRenderer(CustomRender(image))
                            context.source.sender.sendMessage(PluginCommands.getMessage("image-load").replace("%i", view.id.toString()))
                            if (context.source.sender is Player) {
                                val player = context.source.sender as Player
                                val map = ItemStack(Material.FILLED_MAP)
                                val meta = map.itemMeta as MapMeta
                                meta.mapView = view
                                map.itemMeta = meta
                                player.inventory.addItem(map)
                            }
                        }catch (t: Throwable) {
                            context.source.sender.sendMessage(PluginCommands.getMessage("image-load-fail"))
                        }
                        activeRequests--
                    })
                    1
                }
            )
            .build()
    }

    class CustomRender(val image: BufferedImage) : MapRenderer() {
        override fun render(view: MapView, canvas: MapCanvas, player: Player) {
            canvas.drawImage(0, 0, this.image)
            view.isTrackingPosition = false
        }

    }

}