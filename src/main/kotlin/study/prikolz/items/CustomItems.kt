package study.prikolz.items

import org.bukkit.NamespacedKey
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import study.prikolz.Plugin

object CustomItems {

    var registrations = mutableMapOf<String, CustomItem>()
    lateinit var plugin: Plugin
    lateinit var nameKey: NamespacedKey

    fun initialize(plugin: Plugin) {
        this.plugin = plugin
        this.nameKey = NamespacedKey(this.plugin, "customitem")
        register("breaker_bow", BreakerBowItem)
        register("ultimate_breaker_bow", UltimateBreakerBowItem)
        register("wand", RegionStickItem)
    }

    fun register(id: String, customItem: CustomItem): Boolean {
        if (registrations.containsKey(id)) return false
        registrations[id] = customItem
        return true
    }

    fun get(id: String): ItemStack? {
        val stack = registrations[id]?.getStack()?: return null
        val meta = stack.itemMeta
        meta.persistentDataContainer.set(
            this.nameKey,
            PersistentDataType.STRING,
            id
        )
        stack.itemMeta = meta
        return stack
    }

    fun asCustomItem(stack: ItemStack?): CustomItem? {
        val item = stack?: return null
        val meta = item.itemMeta?: return null
        if(!meta.persistentDataContainer.has(this.nameKey)) return null
        val id = meta.persistentDataContainer.get(this.nameKey, PersistentDataType.STRING)?: return null
        val customItem = registrations[id]?: return null
        return customItem
    }

    fun click(event: PlayerInteractEvent) {
        asCustomItem(event.item)?.click(event)
    }

    fun shoot(event: EntityShootBowEvent) {
        asCustomItem(event.bow)?.shoot(event)
    }

    fun drop(event: PlayerDropItemEvent) {
        asCustomItem(event.itemDrop.itemStack)?.drop(event)
    }

    fun swap(event: PlayerSwapHandItemsEvent) {
        asCustomItem(event.offHandItem)?.swap(event)
    }
}