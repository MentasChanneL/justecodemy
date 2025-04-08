package study.prikolz

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.UUID

object Config {

    val regions = mutableMapOf<World, Region>()
    val regionWhitelist = mutableListOf<UUID>()
    val commandsMessages = mutableMapOf<String, String>()
    private lateinit var plugin: Plugin

    fun initialize(plugin: Plugin) {
        this.plugin = plugin;
    }

    fun readConfig(): String? {
        val safeMap = mutableMapOf<World, Region>()
        val yml = YamlConfiguration.loadConfiguration( File(plugin.dataFolder, "config.yml") )

        val regions = yml.getConfigurationSection("regions")?: return "Section \"regions\" not found!"
        for (worldName in regions.getKeys(false)) {
            try {
                val world = Bukkit.getWorld(worldName) ?: return "Section regions.${worldName}: world not found!"
                val worldSection =
                    regions.getConfigurationSection(worldName) ?: return "$worldName in regions must be section!"
                val cor1 = worldSection.getIntegerList("1")
                val cor2 = worldSection.getIntegerList("2")
                safeMap[world] = Region(cor1, cor2, world)
            }catch (t: Throwable) { return "Error in section regions.${worldName}: ${t.message}" }
        }
        this.regions.clear()
        for(key in safeMap.keys) safeMap[key]?.also { this.regions[key] = it }

        val commandsMsgs = yml.getConfigurationSection("commands-messages")?: return "Section commands-messages not found!"
        val safeMapMsgs = mutableMapOf<String, String>()
        for (key in commandsMsgs.getKeys(false)) {
            val v = commandsMsgs.getString(key)?: return "Section commands-messages: $key must be String!"
            safeMapMsgs[key] = v
        }
        this.commandsMessages.clear()
        for(key in safeMapMsgs.keys) safeMapMsgs[key]?.also { this.commandsMessages[key] = it }

        return null
    }

    fun readData(): String? {
        val directory = getDataFolder()
        if (!directory.exists() || !directory.isDirectory) return "Folder \"dataFolder\" not found in ${plugin.dataFolder.path}"
        val file = File(directory, "region_whitelist.yml")
        if (!file.exists()) return "File \"region_whitelist.yml\" not found!"
        val yml = YamlConfiguration.loadConfiguration(file)
        this.regionWhitelist.clear()
        for(key in yml.getKeys(false)) this.regionWhitelist.add(UUID.fromString(key))
        return null
    }

    fun initFiles() {
        var checkFile = File(plugin.dataFolder, "config.yml")
        if(!checkFile.exists()) plugin.saveResource("config.yml", false)
        checkFile = getDataFolder()
        if(!checkFile.exists() || !checkFile.isDirectory) checkFile.mkdirs()
        checkFile = File(checkFile, "region_whitelist.yml")
        if(!checkFile.exists()) checkFile.createNewFile()
    }

    fun getDataFolder(): File {
        return File(this.plugin.dataFolder, "dataFolder")
    }

    fun regionWhitelistAdd(uuid: UUID): Boolean {
        this.regionWhitelist.add(uuid)
        val file = File(getDataFolder(), "region_whitelist.yml")
        if (!file.exists()) return false
        val yml = YamlConfiguration.loadConfiguration(file)
        yml.set(uuid.toString(), System.currentTimeMillis())
        yml.save(file)
        return true
    }

    fun regionWhitelistRemove(uuid: UUID): Boolean {
        this.regionWhitelist.remove(uuid)
        val file = File(getDataFolder(), "region_whitelist.yml")
        if (!file.exists()) return false
        val yml = YamlConfiguration.loadConfiguration(file)
        yml.set(uuid.toString(), null)
        yml.save(file)
        return true
    }

}