package study.prikolz.entity

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.ProjectileHitEvent

interface CustomEntity {

    var entity: Entity?

    fun spawn(loc: Location): Entity
    fun set(ent: Entity) { this.entity = ent }
    fun setFlags(flags: List<Int>) {}
    fun tick() {}
    fun projectileHitEvent(event: ProjectileHitEvent) {}
    fun hurtEvent(event: EntityDamageEvent) {}
    fun deathEvent(event: EntityDeathEvent) {}
    fun explodeEvent(event: EntityExplodeEvent) {}
    fun isValid(): Boolean {
        return this.entity?.isValid?: false
    }
}