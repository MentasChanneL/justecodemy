package study.prikolz

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Study : JavaPlugin() {

    companion object {
        lateinit var instance: Study
        lateinit var events: Events
    }

    override fun onEnable() {
        instance = this
        events = Events()
        Bukkit.getPluginManager().registerEvents(events, this)
        this.logger.info(" - Enabled - ")
    }

    override fun onDisable() {
        Bukkit.getScheduler().cancelTask( events.schedulerTick )
        this.logger.info(" - Disabled - ")
    }

}