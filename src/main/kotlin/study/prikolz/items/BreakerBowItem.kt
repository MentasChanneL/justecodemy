package study.prikolz.items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

object BreakerBowItem : CustomItem {
    override fun getStack(): ItemStack {
        val itemStack = ItemStack(Material.BOW)
        val meta = itemStack.itemMeta
        meta.displayName(Component.text("Block breaker").color(TextColor.color(255, 255, 0)))
        itemStack.itemMeta = meta
        return itemStack
    }

    override fun click(event: PlayerInteractEvent) {
        Bukkit.broadcastMessage("custom click")
    }

    override fun shoot(event: EntityShootBowEvent) {
        Bukkit.broadcastMessage("custom shoot")
    }

    override fun drop(event: PlayerDropItemEvent) {
        Bukkit.broadcastMessage("custom drop")
    }
}