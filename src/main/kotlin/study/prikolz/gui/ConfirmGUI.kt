package study.prikolz.gui

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.inventory.Inventory
import study.prikolz.util.ItemUtils

class ConfirmGUI(private var holder: Player, private val msg: Component, private val yes: Runnable, private val no: Runnable) : CustomGUI() {

    override var inv: Inventory = fill()

    private fun fill(): Inventory {
        val inv = Bukkit.createInventory(null, 27, this.msg)
        fastStack(11, inv, Material.LIME_CONCRETE, ItemUtils.text("✔ Confirm", 0, 255, 0))
        fastStack(15, inv, Material.RED_CONCRETE, ItemUtils.text("❌ Decline", 255, 0, 0))
        return inv
    }

    override fun open(player: Player) {
        this.holder = player
        player.openInventory(this.inv)
    }

    override fun click(isInventory: Boolean, slot: Int, action: InventoryAction): Boolean {
        if (isInventory) return false
        if (slot == 11) {
            playClick()
            this.yes.run()
            return false
        }
        if (slot == 15) {
            playClick()
            this.no.run()
            return false
        }
        return false
    }

    private fun playClick() {
        this.holder.playSound(this.holder, "minecraft:ui.button.click", 0.5f, 2f)
    }
}