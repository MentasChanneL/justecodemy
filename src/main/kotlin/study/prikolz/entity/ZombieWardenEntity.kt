package study.prikolz.entity

import com.destroystokyo.paper.ParticleBuilder
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Zombie
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import java.util.*

class ZombieWardenEntity(var patrol: Patrol) : CustomEntity {

    companion object {
        val patrols = mutableMapOf<String, Patrol>()
        val particleTick = ParticleBuilder(Particle.SMOKE).extra(0.0).offset(0.5, 0.0, 0.5)
    }

    override var entity: Entity? = null
    var target = 0
    var mission: Mission? = null
    var enemy: LivingEntity? = null

    override fun spawn(loc: Location): Entity {
        this.entity = loc.world.spawnEntity(loc, EntityType.ZOMBIE)
        this.entity!!.isInvulnerable = true
        this.entity!!.isSilent = true
        return this.entity!!
    }

    override fun tick() {
        this.entity?.also { particleTick.location(it.location.add(0.0, 2.0, 0.0)).spawn() }
        this.mission?.also {
            if (it.tick(this)) this.mission = null
            return
        }
        this.mission = PatrolMission
    }

    override fun targetEvent(event: EntityTargetLivingEntityEvent) {
        event.target?.also {
            if (it.uniqueId == this.patrol.owner) event.isCancelled = true
        }
    }

    fun getPoint(): Location? {
        if (this.target >= this.patrol.points.size) this.target = 0
        if (this.target >= this.patrol.points.size) return null
        return this.patrol.points[this.target]
    }

    interface Mission {
        fun tick(warden: ZombieWardenEntity): Boolean
    }

    object PatrolMission : Mission {

        override fun tick(warden: ZombieWardenEntity): Boolean {
            val ent = warden.entity?: return false
            if (ent !is Zombie) return false
            val zombie: Zombie = ent
            val moveTarget = warden.getPoint()?: return false
            val enemies = moveTarget.getNearbyEntities(5.0, 5.0, 5.0)
            enemies.removeIf { it.uniqueId == warden.patrol.owner || it.uniqueId == zombie.uniqueId || it !is LivingEntity }
            for (enemy in enemies) {
                warden.enemy = enemy as LivingEntity
                zombie.target = enemy
                zombie.pathfinder.stopPathfinding()
                warden.mission = AttackMission
                return false
            }
            val dist = moveTarget.distance(zombie.location)
            if (dist < 1.5) warden.target++
            zombie.pathfinder.moveTo(moveTarget)
            return false
        }

    }

    object AttackMission : Mission {
        override fun tick(warden: ZombieWardenEntity): Boolean {
            val ent = warden.entity?: return false
            if (ent !is Zombie) return false
            val zombie: Zombie = ent
            val enemy: LivingEntity = warden.enemy?: return true
            val defensePos = warden.getPoint()?: return false
            val dist = defensePos.distance(enemy.location)
            if (dist > 5) {
                zombie.target = null
                return true
            }
            if (zombie.target?.uniqueId != enemy.uniqueId) zombie.target = enemy
            return false
        }

    }

    class Patrol(val owner: UUID) {
        val points = mutableListOf<Location>()
        val wardens = mutableListOf<ZombieWardenEntity>()
    }
}