package study.prikolz.items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.ItemStack
import study.prikolz.entity.BreakerArrowEntity
import study.prikolz.entity.CustomEntities

object BreakerBowItem : CustomItem {

    override fun getStack(): ItemStack {
        val itemStack = ItemStack(Material.BOW)
        val meta = itemStack.itemMeta
        meta.displayName(Component.text("Block breaker").color(TextColor.color(255, 255, 0)))
        itemStack.itemMeta = meta
        return itemStack
    }

    override fun shoot(event: EntityShootBowEvent) {
        CustomEntities.toCustom(BreakerArrowEntity(), event.projectile, mutableListOf())
        event.entity.world.playSound(event.entity.location, "minecraft:entity.witch.throw", 1f, 2f)
    }
}