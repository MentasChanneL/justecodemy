package study.prikolz.items

import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

interface CustomItem {
    fun getStack(): ItemStack
    fun click(event: PlayerInteractEvent)
    fun shoot(event: EntityShootBowEvent)
    fun drop(event: PlayerDropItemEvent)
}