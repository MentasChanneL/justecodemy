package study.prikolz.gui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import study.prikolz.command.PatrolCommand
import study.prikolz.entity.ZombieWardenEntity

class PatrolsGUI(private var holder: Player) : CustomGUI() {

    override var inv: Inventory = fill()
    private var updateCooldown = 0

    private val headValues = mutableMapOf<Int, String>()

    private fun fill(): Inventory {
        val inv = Bukkit.createInventory(null, 27, Component.text("Информация - Патрули"))
        val item = fastStack(0, inv, Material.BLACK_STAINED_GLASS_PANE, Component.empty())
        for (i in 1..7) inv.setItem(i, item)
        fastStack(8, inv, Material.ARROW, Component.text("Back to menu").decoration(TextDecoration.ITALIC, false))
        return inv
    }

    override fun open(player: Player) {
        this.holder = player
        this.updateCooldown = 0
        update()
        player.openInventory(this.inv)
    }

    override fun tick() {
        if (this.updateCooldown > Bukkit.getCurrentTick()) return
        this.updateCooldown = Bukkit.getCurrentTick() + 20
        update()
    }

    override fun click(isInventory: Boolean, slot: Int, action: InventoryAction): Boolean {
        if (isInventory) return false
        if (slot == 8) {
            playClick()
            openGUI(this.holder, InfoGUI(this.holder))
            return false
        }
        if (action != InventoryAction.PICKUP_HALF) return false
        headValues[slot]?.also {
            val confirm = ConfirmGUI(
                this.holder,
                Component.text("Delete $it patrol?"),
                {
                    PatrolCommand.runCommand(this.holder, it, PatrolCommand.PatrolAction.REMOVE)
                    openGUI(this.holder, this)
                },
                {
                    openGUI(this.holder, this)
                }
            )
            openGUI(this.holder, confirm)
        }
        return false
    }

    private fun update() {
        headValues.clear()
        var slot = 9
        for ((name, patrol) in ZombieWardenEntity.patrols) {
            if (patrol.owner != this.holder.uniqueId) continue
            val head = ItemStack(Material.ZOMBIE_HEAD)
            val meta = head.itemMeta
            meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false))
            val lore = mutableListOf<Component>()
            val zombieLoc: Location? = patrol.wardens.first().entity?.location
            lore.add( normalText("x: ${zombieLoc?.blockX?: "?"} y: ${zombieLoc?.blockY?: "?"} z: ${zombieLoc?.blockZ?: "?"}", 100, 255, 0) )
            lore.add( normalText("Points count: ${patrol.points.size}", 0, 255, 100) )
            meta.lore(lore)
            head.itemMeta = meta
            this.inv.setItem(slot, head)
            headValues[slot] = name
            slot++
            if (slot > 25) {
                fastStack(26, this.inv, Material.BARRIER, normalText("Too many patrols...", 255, 100, 0))
                break
            }
        }
        if (slot > 25) return
        for (i in slot..26) {
            inv.setItem(i, null)
        }
    }

    private fun playClick() {
        this.holder.playSound(this.holder, "minecraft:ui.button.click", 0.5f, 2f)
    }
}