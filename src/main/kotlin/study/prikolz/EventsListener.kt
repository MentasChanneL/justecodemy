package study.prikolz

import com.destroystokyo.paper.ParticleBuilder
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Sheep
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import study.prikolz.entity.CustomEntities
import study.prikolz.gui.CustomGUI
import study.prikolz.items.CustomItems
import java.util.*

object EventsListener : Listener {

    interface LifeTimeObject {
        var life: Int
        fun run()
        fun end()
    }

    private val ownedPigs = mutableMapOf<UUID, UUID>()
    private val superJumpCDs = mutableMapOf<UUID, Int>()
    private val lifeTimeEntities = mutableMapOf<Entity, Int>()
    private val lifeTimeObjects = mutableListOf<LifeTimeObject>()
    private val prefixes = mutableMapOf<UUID, Component>()

    private const val SUPER_JUMP_CD = 1400

    private var schedulerTick: Int = -1
    private lateinit var plugin: Plugin

    fun initialize(plugin: Plugin) {
        schedulerTick = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, { tick() }, 1, 1)
        this.plugin = plugin
    }

    private fun formatTick(tick: Int): String {
        val minutes = tick / 1200
        val seconds = tick % 1200 / 20
        val result = if (minutes < 10) "0${minutes}:" else "${minutes}:"
        return if (seconds < 10) "${result}0${seconds}" else "${result}${seconds}"
    }

    private fun superJumpTick(uuid: UUID) {
        superJumpCDs[uuid]?.also {
            val player = Bukkit.getPlayer(uuid)
            if (player == null || !player.isValid) {
                superJumpCDs.remove(uuid)
                return
            }
            if (it > Bukkit.getServer().currentTick) {
                player.sendActionBar(
                    Component.text(formatTick(it - Bukkit.getServer().currentTick))
                )
                return
            }
            superJumpCDs.remove(uuid)
            player.sendActionBar(Component.empty())
        }
    }

    private fun lifeTimeEntityTick(ent: Entity) {
        lifeTimeEntities[ent]?.let {
            if (it < 1 || !ent.isValid) {
                lifeTimeEntities.remove(ent)
                if (ent.isValid) ent.remove()
                return
            }
            lifeTimeEntities[ent] = it - 1
        }
    }

    private fun lifeTimeObjectTick(index: Int, obj: LifeTimeObject): Int {
        obj.life--
        if (obj.life < 1) {
            lifeTimeObjects.removeAt(index)
            obj.end()
            return index
        }
        obj.run()
        return index + 1
    }

    private fun tick() {
        try { CustomEntities.tick() } catch (e: Throwable) { this.plugin.logger.severe("CustomEntities.tick: ${e.message}") }
        for (uuid in superJumpCDs.keys.toList()) superJumpTick(uuid)
        for (ent in lifeTimeEntities.keys.toList()) lifeTimeEntityTick(ent)
        var i = 0
        while (i < lifeTimeObjects.size) i = lifeTimeObjectTick(i, lifeTimeObjects[i])
    }

    @EventHandler
    fun playerJoin(event: PlayerJoinEvent) {
        event.player.sendMessage(Component.text("Привет!"))
        ownedPigs[event.player.uniqueId]?.also {
            val ent = Bukkit.getEntity(it)
            ent?.remove()
            ownedPigs.remove(event.player.uniqueId)
        }
        prefixes[event.player.uniqueId] = Component.text("[Игрок] ").color(TextColor.color(150, 150, 150))
        event.joinMessage(Component.text("${event.player.name} join on server!").color(TextColor.color(0, 255, 0)))
    }

    @EventHandler
    fun playerLeave(event: PlayerQuitEvent) {
        val ent = event.player.world.spawnEntity(
            event.player.location, EntityType.PIG
        ) as LivingEntity
        ent.setAI(false)
        event.quitMessage(Component.empty())
        ownedPigs[event.player.uniqueId] = ent.uniqueId
    }

    @EventHandler
    fun playerMsg(event: AsyncChatEvent) {
        prefixes[event.player.uniqueId]?.also {
            Bukkit.broadcast(it.append(event.player.name()).append(Component.text(" > ")).append(event.message()))
        }
    }

    @EventHandler
    fun playerBlockBreak(event: BlockBreakEvent) {
        if (!Region.isAllowOnRegion(event.block.location, event.player.uniqueId)) {
            event.isCancelled = true
            return
        }
        if (event.block.type == Material.STONE) event.isCancelled = true
        Scores.addScore(event.player, event.block.type)
    }

    @EventHandler
    fun playerBlockPlace(event: BlockPlaceEvent) {
        if (!Region.isAllowOnRegion(event.block.location, event.player.uniqueId)) {
            event.isCancelled = true
            return
        }
        event.player.inventory.addItem(ItemStack(event.block.type, 1))
    }

    @EventHandler
    fun playerInteract(event: PlayerInteractEvent) {
        CustomItems.click(event)
        val block = event.clickedBlock
        if (block?.type == Material.SHORT_GRASS) {
            block.type = Material.AIR
            val world = block.world
            world.spawnEntity(block.location.add(Vector(0.5, 0.5, 0.5)), EntityType.SHEEP)
        }
    }

    @EventHandler
    fun playerEntityInteract(event: PlayerInteractEntityEvent) {
        val ent = event.rightClicked
        if (ent is Sheep) ent.color = DyeColor.PURPLE
    }

    @EventHandler
    fun playerMove(event: PlayerMoveEvent) {
        val player = event.player
        for (ent in player.world.entities) {
            if (ent !is Sheep) continue
            val dist = ent.location.distance(player.location)
            if (dist < 1.5) {
                val punch = player.location.toVector().subtract(ent.location.toVector())
                player.velocity = player.velocity.add(punch)
            }
        }
        if (event.hasChangedBlock()) {
            val endSpawn = ParticleBuilder(Particle.END_ROD)
            endSpawn.location(player.location)
            endSpawn.offset(0.0, 0.0, 0.0)
            endSpawn.count(2)
            endSpawn.extra(0.1)
            endSpawn.allPlayers()

            val tickSpawn = ParticleBuilder(Particle.ELECTRIC_SPARK)
            tickSpawn.count(1)
            tickSpawn.extra(0.0)
            tickSpawn.location(player.location)
            lifeTimeObjects.add(object : LifeTimeObject {

                private val endSpawn = endSpawn
                private val tickSpawn = tickSpawn
                override var life: Int = 200

                override fun run() {
                    this.tickSpawn.spawn()
                }

                override fun end() {
                    this.endSpawn.spawn()
                }
            })
        }
    }

    @EventHandler
    fun playerShift(event: PlayerToggleSneakEvent) {
        if (event.isSneaking && !superJumpCDs.containsKey(event.player.uniqueId)) {
            event.player.velocity = event.player.velocity.add(Vector(0.0, 1.5, 0.0))
            superJumpCDs[event.player.uniqueId] = Bukkit.getServer().currentTick + SUPER_JUMP_CD
        }
    }

    @EventHandler
    fun playerClickInventory(event: InventoryClickEvent) {
        if(event.whoClicked !is Player) return
        val player = event.whoClicked as Player
        val clicked = event.clickedInventory?: return
        CustomGUI.holders[player]?.also {
            val isMine = clicked.type == InventoryType.PLAYER
            if (!it.click(isMine, event.slot, event.action)) event.isCancelled = true
        }
    }

    @EventHandler
    fun playerCloseInventory(event: InventoryCloseEvent) {
        if(event.player !is Player) return
        val player = event.player as Player
        CustomGUI.holders[player]?.also {
            CustomGUI.holders.remove(player)
            it.close()
        }
    }

    @EventHandler
    fun playerDropEvent(event: PlayerDropItemEvent) {
        CustomItems.drop(event)
    }

    @EventHandler
    fun entityBowEvent(event: EntityShootBowEvent) {
        CustomItems.shoot(event)
    }

    @EventHandler
    fun entityDeath(event: EntityDeathEvent) {
        CustomEntities.deathEvent(event)
        if (event.entity.type != EntityType.SHEEP) return
        val cause = event.damageSource.causingEntity
        cause?.also {
            if (cause !is Player) return
            val sword = ItemStack(Material.DIAMOND_SWORD)
            val meta = sword.itemMeta
            val enchants = listOf<Enchantment>(
                Enchantment.SHARPNESS,
                Enchantment.SMITE,
                Enchantment.KNOCKBACK,
                Enchantment.IMPALING
            )
            meta.addEnchant(
                enchants[Random().nextInt(enchants.size)],
                Random().nextInt(5) + 1,
                true
            )
            sword.itemMeta = meta
            event.drops.clear()
            val dropped = event.entity.world.dropItemNaturally(event.entity.location, sword)
            lifeTimeEntities[dropped] = 40
        }
    }

    @EventHandler
    fun entityDamage(event: EntityDamageEvent) {
        CustomEntities.hurtEvent(event)
    }

    @EventHandler
    fun projectileHit(event: ProjectileHitEvent) {
        CustomEntities.projectileHitEvent(event)
    }

    @EventHandler
    fun entityExplode(event: EntityExplodeEvent) {
        CustomEntities.explodeEvent(event)
    }
}