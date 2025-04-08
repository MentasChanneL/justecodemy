package study.prikolz.gui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

open class CustomGUI {

    companion object {
        val holders = mutableMapOf<Player, CustomGUI>()

        fun openGUI(player: Player, gui: CustomGUI) {
            gui.open(player)
            holders[player] = gui
        }

        fun tick() {
            try { for (inv in holders.values) inv.tick() } catch (t: Throwable) { throw RuntimeException("CustomGUI tick exception: ${t.message}") }
        }

        fun fastStack(slot: Int, inv: Inventory, mat: Material, name: Component): ItemStack {
            val item = ItemStack(mat)
            val meta = item.itemMeta
            meta.displayName(name)
            item.itemMeta = meta
            inv.setItem(slot, item)
            return item
        }

        fun itemSetName(item: ItemStack, name: Component) {
            val meta = item.itemMeta
            meta.displayName(name)
            item.itemMeta = meta
        }

        fun normalText(text: String, r: Int, g: Int, b: Int): Component {
            return Component.text(text).color(TextColor.color(r, g, b)).decoration(TextDecoration.ITALIC, false)
        }

    }

    open var inv: Inventory = Bukkit.createInventory(null, InventoryType.CHEST, Component.text("Default custom gui"))

    open fun open(player: Player) {
        player.openInventory(inv)
    }

    open fun tick() { }

    open fun click(isInventory: Boolean, slot: Int, action: InventoryAction): Boolean {
        return false
    }

    open fun close() { }
}