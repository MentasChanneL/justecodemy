package study.prikolz.gui

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class InvSeeGUI(private val target: Player) : CustomGUI() {
    override var inv: Inventory = fill()

    private fun fill(): Inventory {
        val inv = Bukkit.createInventory(null, 54, Component.text("Инвентарь игрока ${this.target.name}"))
        val nullStack = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
        val meta = nullStack.itemMeta
        meta.displayName(Component.empty())
        nullStack.itemMeta = meta
        for (a in 0..53) {
            inv.setItem(a, nullStack)
        }
        var i = 0
        for (item in target.inventory.contents) {
            if(i in 0..8) inv.setItem(i + 45, item)
            if(i in 9..35) inv.setItem(i + 9, item)
            if(i in 36..39) inv.setItem(i - 36, item)
            if(i == 40) inv.setItem(i - 35, item)
            i++;
        }
        return inv;
    }

}