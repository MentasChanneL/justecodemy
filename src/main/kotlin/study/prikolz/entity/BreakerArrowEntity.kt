package study.prikolz.entity

import com.destroystokyo.paper.ParticleBuilder
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import study.prikolz.Scores

class BreakerArrowEntity : CustomEntity {

    private val flameTick = init()
    override var entity: Entity? = null
    var explode = false
    var owner: Player? = null

    private fun init(): ParticleBuilder {
        val builder = ParticleBuilder(Particle.FLAME)
        builder.count(2)
        builder.extra(0.02)
        builder.offset(0.1, 0.1, 0.1)
        return builder
    }

    override fun spawn(loc: Location): Entity {
        this.entity = loc.world.spawnEntity(loc, EntityType.ARROW)
        return this.entity!!
    }

    override fun setFlags(flags: List<Int>) {
        if(flags.contains(1)) {
            this.explode = true
            this.flameTick.extra(0.03).offset(0.2, 0.2, 0.2).count(3)
        }
        if(flags.contains(0)) {
            this.explode = false
            this.flameTick.extra(0.02).offset(0.1, 0.1, 0.1).count(2)
        }
    }

    override fun tick() {
        if (!isValid()) return
        val valid = this.entity?: return
        this.flameTick.location(valid.location).spawn()
    }

    override fun projectileHitEvent(event: ProjectileHitEvent) {
        if (!isValid()) return
        val valid = this.entity?: return
        if (event.hitEntity != null) {
            event.isCancelled = true
            return
        }
        val block = event.hitBlock?: return
        val player: Player? = Bukkit.getPlayer( event.entity.ownerUniqueId?: event.entity.uniqueId )
        player?.also { Scores.addScore(it, block.type) }
        block.breakNaturally()
        if (this.explode) {
            this.owner = player
            valid.world.createExplosion(valid, 4f, false)
        }
        valid.location.world.dropItemNaturally(valid.location, ItemStack(Material.ARROW))
        valid.remove()
    }

    override fun explodeEvent(event: EntityExplodeEvent) {
        for(block in event.blockList()) {
            this.owner?.also { Scores.addScore(it, block.type) }
            for (item in block.drops) event.entity.world.dropItemNaturally(event.location, item).also{
                it.isInvulnerable = true
                it.velocity = it.velocity.add(Vector(0.0, 0.5, 0.0))
            }
            block.setType(Material.AIR, true)
        }
        event.entity.world.playSound(event.location, "minecraft:entity.wither.break_block", 1f, 1f)
        event.blockList().clear()
    }
}