package study.prikolz.items

import org.bukkit.Material
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import study.prikolz.Selection
import study.prikolz.util.ItemUtils

object RegionStickItem : CustomItem {

    override fun getStack(): ItemStack {
        val item = ItemStack(Material.STICK)
        ItemUtils.rename(item, "Region Editor", 120, 125, 0)
        return item
    }

    override fun click(event: PlayerInteractEvent) {
        event.isCancelled = true
        if (event.player.hasCooldown(Material.STICK)) return
        event.player.setCooldown(Material.STICK, 5)
        val block = event.clickedBlock?: return
        val selection = Selection.selections[event.player]?: Selection(block.location, block.location)
        if (event.action == Action.LEFT_CLICK_BLOCK) selection.a = block.location
        if (event.action == Action.RIGHT_CLICK_BLOCK) selection.b = block.location
        if (!Selection.selections.containsKey(event.player)) Selection.selections[event.player] = selection
    }

    override fun swap(event: PlayerSwapHandItemsEvent) {
        event.isCancelled = true
        Selection.selections.remove(event.player)
        event.player.swingMainHand()
        event.player.sendMessage("Region selection cleared")
    }
}