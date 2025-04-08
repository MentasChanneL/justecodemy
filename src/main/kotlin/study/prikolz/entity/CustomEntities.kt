package study.prikolz.entity

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.entity.*
import study.prikolz.Plugin
import java.util.UUID

object CustomEntities {

    private val entities = mutableMapOf<UUID, CustomEntity>()

    lateinit var plugin: Plugin

    fun initialize(plugin: Plugin) {
        this.plugin = plugin
    }

    fun spawn(customEntity: CustomEntity, loc: Location, flags: List<Int>): Entity {
        val ent = customEntity.spawn(loc)
        customEntity.setFlags(flags)
        this.entities[ent.uniqueId] = customEntity
        return ent
    }

    fun toCustom(customEntity: CustomEntity, ent: Entity, flags: List<Int>) {
        customEntity.set(ent)
        customEntity.setFlags(flags)
        this.entities[ent.uniqueId] = customEntity
    }

    fun tick() {
        for(uuid in this.entities.keys.toList()) {
            this.entities[uuid]?.also {
                if(!it.isValid()) {
                    this.entities.remove(uuid)
                } else {
                    try { it.tick() } catch (t: Throwable) { this.plugin.logger.severe("CustomEntities.tick.$uuid $it: ${t.message}") }
                }
            }
        }
    }

    fun projectileHitEvent(event: ProjectileHitEvent) {
        asCustomEntity(event.entity)?.projectileHitEvent(event)
    }
    fun hurtEvent(event: EntityDamageEvent) {
        asCustomEntity(event.entity)?.hurtEvent(event)
    }
    fun deathEvent(event: EntityDeathEvent) {
        asCustomEntity(event.entity)?.deathEvent(event)
    }
    fun explodeEvent(event: EntityExplodeEvent) {
        asCustomEntity(event.entity)?.explodeEvent(event)
    }
    fun entityTarget(event: EntityTargetLivingEntityEvent) {
        asCustomEntity(event.entity)?.targetEvent(event)
    }

    fun asCustomEntity(entity: Entity?): CustomEntity? {
        val ent = entity?: return null
        return entities[ent.uniqueId]
    }
}