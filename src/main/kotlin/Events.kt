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
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*

const val SUPER_JUMP_CD = 1400

interface LifeTimeRunnable {
    fun getLife(): Int
    fun subLife(amount: Int)
    fun run()
    fun end()
}

class Events : Listener {

    private val ownedPigs = mutableMapOf<UUID, UUID>()
    private val superJumpCD = mutableMapOf<UUID, Int>()
    private val lifeTimeEntity = mutableMapOf<Entity, Int>()
    private val lifeTimeRunnable = mutableListOf<LifeTimeRunnable>()
    private val prefix = mutableMapOf<UUID, Component>()

    val schedulerTick: Int

    init {
        this.schedulerTick = Bukkit.getScheduler().scheduleSyncRepeatingTask(Study.instance, { tick() }, 1, 1)
    }

    fun formatTick(tick: Int): String {
        var result = ""
        val minutes = tick / 1200
        val seconds = tick % 1200 / 20
        result = if(minutes < 10) "0$minutes:" else "$minutes:"
        return if(seconds < 10) "${result}0${seconds}" else "${result}${seconds}"
    }

    private fun tick() {
        for (uuid in superJumpCD.keys) {
            if (superJumpCD[uuid]!! > Bukkit.getServer().currentTick) {
                val player = Bukkit.getPlayer(uuid)
                player?.let {
                    if(it.isValid) it.sendActionBar(
                        Component.text(formatTick( superJumpCD[uuid]!! - Bukkit.getServer().currentTick ))
                    )
                }
            }else{
                val player = Bukkit.getPlayer(uuid)
                player?.let { if(it.isValid) it.sendActionBar(Component.empty()) }
                superJumpCD.remove(uuid)
            }
        }
        val blacklist = mutableListOf<Entity>()
        for(ent in lifeTimeEntity.keys) {
            val life = lifeTimeEntity[ent]!!
            if (life < 1 || !ent.isValid) {
                blacklist.add(ent)
                if (ent.isValid) ent.remove()
                continue
            }
            lifeTimeEntity[ent] = life - 1
        }
        for(ent in blacklist) lifeTimeEntity.remove(ent)

        var i = 0;
        while(i < lifeTimeRunnable.size) {
            val run = lifeTimeRunnable[i]
            run.subLife(1)
            if(run.getLife() < 1) {
                lifeTimeRunnable.removeAt(i)
                run.end()
                continue
            }
            run.run()
            i++
        }

    }

    @EventHandler
    fun playerJoin(e: PlayerJoinEvent) {
        e.player.sendMessage(Component.text("Привет!"))
        ownedPigs[e.player.uniqueId]?.let {
            val ent = Bukkit.getEntity(it)
            ent!!.remove()
            ownedPigs.remove(e.player.uniqueId)
        }
        prefix[e.player.uniqueId] = Component.text("[Игрок] ").color(TextColor.color(150, 150, 150))
        e.joinMessage(Component.text("${e.player.name} join on server!").color(TextColor.color(0, 255, 0)))
    }

    @EventHandler
    fun playerLeave(e: PlayerQuitEvent) {
        val ent = e.player.world.spawnEntity(
            e.player.location, EntityType.PIG
        ) as LivingEntity
        ent.setAI(false)
        e.quitMessage(Component.empty())
        ownedPigs[e.player.uniqueId] = ent.uniqueId
    }

    @EventHandler
    fun playerMsg(e: AsyncChatEvent) {
        prefix[e.player.uniqueId]?.let {
            e.isCancelled = true;
            Bukkit.broadcast(it.append(e.player.name()).append(Component.text(" > ")).append(e.message()))
        }
    }

    @EventHandler
    fun playerBlockBreak(e: BlockBreakEvent) {
        if ( e.block.type == Material.STONE ) e.isCancelled = true
    }

    @EventHandler
    fun playerBlockPlace(e: BlockPlaceEvent) {
        e.player.inventory.addItem( ItemStack(e.block.type, 1) )
    }

    @EventHandler
    fun playerInteract(e: PlayerInteractEvent) {
        if (e.clickedBlock == null) return
        val block = e.clickedBlock
        if (block!!.type == Material.SHORT_GRASS) {
            block.type = Material.AIR
            val world = block.world
            world.spawnEntity( block.location.add(Vector(0.5, 0.5 ,0.5)), EntityType.SHEEP )
        }
    }

    @EventHandler
    fun playerEntityInteract(e: PlayerInteractEntityEvent) {
        val ent = e.rightClicked
        if ( ent.type == EntityType.SHEEP ) {
            val sheep = ent as Sheep
            sheep.color = DyeColor.PURPLE
        }
    }

    @EventHandler
    fun playerMove(e: PlayerMoveEvent) {
        val player = e.player
        for (ent in player.world.entities) {
            if (ent !is Sheep) continue
            val dist = ent.location.distance(player.location)
            if (dist < 1.5) {
                val punch = player.location.toVector().subtract( ent.location.toVector() )
                player.velocity = player.velocity.add(punch)
            }
        }
        if (e.hasChangedBlock() ) {
            val builder = ParticleBuilder(Particle.END_ROD)
            builder.location(player.location)
            builder.offset(0.0, 0.0, 0.0)
            builder.count(2)
            builder.extra(0.1)
            builder.allPlayers()

            val state = builder.clone()
            state.count(1)
            state.extra(0.0)
            lifeTimeRunnable.add(object : LifeTimeRunnable {

                private var life = 200
                private val builder = builder
                private val state = state

                override fun getLife(): Int {
                    return this.life
                }

                override fun subLife(amount: Int) {
                    this.life--
                }

                override fun run() {
                    this.state.spawn()
                }

                override fun end() {
                    this.builder.spawn()
                }
            })
        }
    }

    @EventHandler
    fun playerShift(e: PlayerToggleSneakEvent) {
        if (e.isSneaking && !superJumpCD.containsKey(e.player.uniqueId)) {
            e.player.velocity = e.player.velocity.add(Vector(0.0, 1.5, 0.0))
            superJumpCD[e.player.uniqueId] = Bukkit.getServer().currentTick + SUPER_JUMP_CD
        }
    }

    @EventHandler
    fun entityDeath(e: EntityDeathEvent) {
        if (e.entity.type != EntityType.SHEEP) return
        val cause = e.damageSource.causingEntity
        cause?.let {
            if (cause !is Player) return
            val sword = ItemStack(Material.DIAMOND_SWORD)
            val meta = sword.itemMeta
            val enchants = listOf<Enchantment>( Enchantment.SHARPNESS, Enchantment.SMITE, Enchantment.KNOCKBACK, Enchantment.IMPALING )
            meta.addEnchant(
                enchants[Random().nextInt(enchants.size)],
                Random().nextInt(5) + 1,
                true
            )
            sword.itemMeta = meta
            e.drops.clear()
            val dropped = e.entity.world.dropItemNaturally(e.entity.location, sword)
            lifeTimeEntity[dropped] = 40
        }
    }
}