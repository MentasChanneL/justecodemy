package study.prikolz.gui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.inventory.Inventory
import study.prikolz.command.BowCommand
import study.prikolz.command.ParticlesCommand
import study.prikolz.command.TPSCommand
import study.prikolz.items.CustomItems

class InfoGUI(private var holder: Player) : CustomGUI() {

    override var inv: Inventory = fill()
    private var updateCooldown = 0
    private val clickable = mutableMapOf<Int, () -> Unit>(
        Pair(10) { TPSCommand.runCommand(this.holder) },
        Pair(11) { clickParticles() },
        Pair(12) { BowCommand.runCommand(this.holder, false) },
        Pair(13) { BowCommand.runCommand(this.holder, true) },
        Pair(16) { clickPatrols() }
    )
    private val tpsStack = fastStack(10, this.inv, Material.COMMAND_BLOCK, Component.text("..."))
    private val particlesStack = fastStack(11, this.inv, Material.PRISMARINE_CRYSTALS, Component.text("Particles: "))

    private fun fill(): Inventory {
        val inv = Bukkit.createInventory(null, 27, Component.text("- Информация"))
        inv.setItem(12, CustomItems.get("breaker_bow"))
        inv.setItem(13, CustomItems.get("ultimate_breaker_bow"))
        fastStack(16, inv, Material.ZOMBIE_HEAD, Component.text("My patrols").decoration(TextDecoration.ITALIC, false))
        return inv
    }

    override fun open(player: Player) {
        this.holder = player
        this.updateCooldown = 0
        updateParticlesStack()
        player.openInventory(this.inv)
    }

    override fun tick() {
        if (this.updateCooldown > Bukkit.getCurrentTick()) return
        this.updateCooldown = Bukkit.getCurrentTick() + 20
        itemSetName(tpsStack, TPSCommand.tpsComponent().decoration(TextDecoration.ITALIC, false))
        this.inv.setItem(10, tpsStack)
    }

    override fun click(isInventory: Boolean, slot: Int, action: InventoryAction): Boolean {
        if (isInventory) return false
        clickable[slot]?.invoke()
        return false
    }

    private fun updateParticlesStack() {
        var state = Component.text("Enabled").color(TextColor.color(0, 255, 0))
        if (!ParticlesCommand.get(this.holder.uniqueId)) state = Component.text("Disabled").color(TextColor.color(255, 100, 0))
        itemSetName(this.particlesStack, Component.text("Particles: ").append(state).decoration(TextDecoration.ITALIC, false))
        this.inv.setItem(11, this.particlesStack)
    }

    private fun playClick() {
        this.holder.playSound(this.holder, "minecraft:ui.button.click", 0.5f, 2f)
    }

    private fun clickParticles() {
        ParticlesCommand.runCommand(this.holder, !ParticlesCommand.get(this.holder.uniqueId))
        playClick()
        updateParticlesStack()
    }

    private fun clickPatrols() {
        playClick()
        openGUI(this.holder, PatrolsGUI(this.holder))
    }
}