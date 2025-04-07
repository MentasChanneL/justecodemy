package study.prikolz.gui

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

open class CustomGUI {

    companion object {
        val holders = mutableMapOf<Player, CustomGUI>()

        fun openGUI(player: Player, gui: CustomGUI) {
            gui.open(player)
            holders[player] = gui
        }

    }

    open var inv: Inventory = Bukkit.createInventory(null, InventoryType.CHEST, Component.text("Default custom gui"))

    open fun open(player: Player) {
        player.openInventory(inv)
    }

    open fun click(isInventory: Boolean, slot: Int, action: InventoryAction): Boolean {
        return false
    }

    open fun close() { }
}