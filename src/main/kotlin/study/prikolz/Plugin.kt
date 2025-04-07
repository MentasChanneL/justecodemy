package study.prikolz

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import study.prikolz.entity.CustomEntities
import study.prikolz.items.CustomItems

class Plugin : JavaPlugin() {

    override fun onEnable() {
        Config.initialize(this)
        Config.initFiles()
        Bukkit.getPluginManager().registerEvents(EventsListener, this)
        EventsListener.initialize(this)
        Scores.initialization(this)
        CustomEntities.initialize(this)
        CustomItems.initialize(this)
        this.saveResource("config.yml", false)
        Config.readConfig().also{ if(it != null) this.logger.warning("Config error: $it") }
        Config.readData().also{ if(it != null) this.logger.warning("Data read error: $it") }
        PluginCommands.register(this)
        this.logger.info(" - Enabled - ")
    }

    override fun onDisable() {
        this.logger.info(" - Disabled - ")
    }

}