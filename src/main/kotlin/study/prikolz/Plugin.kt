package study.prikolz

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Plugin : JavaPlugin() {

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(EventsListener, this)
        EventsListener.initialize(this)
        this.logger.info(" - Enabled - ")
    }

    override fun onDisable() {
        this.logger.info(" - Disabled - ")
    }

}