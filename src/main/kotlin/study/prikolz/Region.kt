package study.prikolz

import org.bukkit.Location
import org.bukkit.World
import java.util.*

class Region(pos1: List<Int>, pos2: List<Int>, val world: World) {

    private val minX: Int = Math.min(pos1[0], pos2[0])
    private val minY: Int = Math.min(pos1[1], pos2[1])
    private val minZ: Int = Math.min(pos1[2], pos2[2])

    private val maxX: Int = Math.max(pos1[0], pos2[0])
    private val maxY: Int = Math.max(pos1[1], pos2[1])
    private val maxZ: Int = Math.max(pos1[2], pos2[2])

    fun inRegion(check: Location): Boolean {
        if (check.world != world) return false
        return check.blockX in minX .. maxX && check.blockY in minY .. maxY && check.blockZ in minZ .. maxZ
    }

    companion object {
        fun isAllowOnRegion(loc: Location, by: UUID): Boolean {
            if (Config.regionWhitelist.contains(by)) return true
            for (region in Config.regions.values) {
                if (region.inRegion(loc)) return false
            }
            return true
        }
    }

}