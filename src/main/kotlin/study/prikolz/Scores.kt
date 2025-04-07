package study.prikolz

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard
import java.util.UUID

object Scores {

    private lateinit var plugin: Plugin
    private val scores = mutableMapOf<UUID, Int>()
    private val scoreBoardsHolders = mutableMapOf<UUID, Scoreboard>()

    private val blocksScores = mutableMapOf(
        Pair(Material.DIRT, 2),
        Pair(Material.STONE, 3),
        Pair(Material.OAK_LOG, 4)
    )

    fun initialization(plugin: Plugin) {
        this.plugin = plugin
    }

    private fun playerUpdate(player: Player) {
        if (scoreBoardsHolders[player.uniqueId] == null) {
            val board = Bukkit.getScoreboardManager().newScoreboard
            player.scoreboard = board
            scoreBoardsHolders[player.uniqueId] = board
            val obj = board.getObjective("score")?: board.registerNewObjective("score", Criteria.DUMMY, Component.text("    Mini Gayme    ").color(
                TextColor.color(255, 255, 0)))
            obj.displaySlot = DisplaySlot.SIDEBAR
            obj.setAutoUpdateDisplay(true)
        }
        val board: Scoreboard = scoreBoardsHolders[player.uniqueId]?: return
        board.getObjective("score")?.getScore("Score")?.score = this.scores[player.uniqueId]?: 0
    }

    fun addScore(player: Player, block: Material) {
        this.scores[player.uniqueId] = (this.scores[player.uniqueId]?: 0) + (blocksScores[block]?: 1)
        playerUpdate(player)
    }

    fun addScore(player: Player, amount: Int) {
        this.scores[player.uniqueId] = (this.scores[player.uniqueId]?: 0) + amount
        playerUpdate(player)
    }

    fun setScore(player: Player, amount: Int) {
        this.scores[player.uniqueId] = amount
        playerUpdate(player)
    }
}