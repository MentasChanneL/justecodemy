package study.prikolz

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective

object Scores {

    private lateinit var plugin: Plugin
    private lateinit var objective: Objective

    private val blocksScores = mutableMapOf(
        Pair(Material.DIRT, 2),
        Pair(Material.STONE, 3),
        Pair(Material.OAK_LOG, 4)
    )

    fun initialization(plugin: Plugin) {
        this.plugin = plugin
        val main = Bukkit.getScoreboardManager().mainScoreboard
        main.getObjective("block_scores")?.unregister()
        this.objective = main.registerNewObjective("block_scores", Criteria.DUMMY, Component.text("Block scoreboard"))
        this.objective.displaySlot = DisplaySlot.SIDEBAR
    }

    fun addScore(player: Player, block: Material) {
        this.objective.getScore(player).score += blocksScores[block]?: 1
    }

    fun addScore(player: Player, amount: Int) {
        this.objective.getScore(player).score += amount
    }

    fun setScore(player: Player, amount: Int) {
        this.objective.getScore(player).score = amount
    }

    fun resetScore(player: Player) {
        this.objective.getScore(player).resetScore()
    }
}