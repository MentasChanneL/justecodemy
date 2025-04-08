package study.prikolz

import com.destroystokyo.paper.ParticleBuilder
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.*

class Selection(var a: Location, var b: Location) {

    companion object {
        val selections = mutableMapOf<Player, Selection>()
        val cuboidOffsets = mutableListOf(
            FromTo( Vector (0, 0, 0), Vector (1, 0, 0) ),
            FromTo( Vector (0, 0, 0), Vector (0, 1, 0) ),
            FromTo( Vector (0, 0, 0), Vector (0, 0, 1) ),

            FromTo( Vector (0, 1, 0), Vector (1, 1, 0) ),
            FromTo( Vector (0, 1, 0), Vector (0, 1, 1) ),

            FromTo( Vector (1, 0, 1), Vector (1, 0, 0) ),
            FromTo( Vector (1, 0, 1), Vector (0, 0, 1) ),

            FromTo( Vector (1, 0, 0), Vector (1, 1, 0) ),
            FromTo( Vector (0, 0, 1), Vector (0, 1, 1) ),

            FromTo( Vector (1, 1, 1), Vector (0, 1, 1) ),
            FromTo( Vector (1, 1, 1), Vector (1, 0, 1) ),
            FromTo( Vector (1, 1, 1), Vector (1, 1, 0) )
        )

        fun tick() {
            for (player in selections.keys.toList()) {
                if (!player.isValid) {
                    selections.keys.remove(player)
                    continue
                }
                selections[player]?.also {
                    it.tick(player)
                }
            }
        }

        fun showBorders(player: Player, a: Location, b: Location, particle: ParticleBuilder) {
            val convertedA = Location(a.world,
                min(a.blockX, b.blockX).toDouble(),
                min(a.blockY, b.blockY).toDouble(),
                min(a.blockZ, b.blockZ).toDouble()
            )
            val convertedB = Location(b.world,
                max(a.blockX, b.blockX).toDouble() + 1,
                max(a.blockY, b.blockY).toDouble() + 1,
                max(a.blockZ, b.blockZ).toDouble() + 1
            )
            val builder = particle.receivers(player)
            val offsets = Vector (convertedB.x - convertedA.x, convertedB.y - convertedA.y, convertedB.z - convertedA.z)
            for (offset in cuboidOffsets) {
                val from = convertedA.clone().add(Vector (offsets.x * offset.from.x, offsets.y * offset.from.y, offsets.z * offset.from.z))
                val to = convertedA.clone().add(Vector (offsets.x * offset.to.x, offsets.y * offset.to.y, offsets.z * offset.to.z))
                val start = Location(from.world,
                    min(from.x, to.x) ,
                    min(from.y, to.y) ,
                    min(from.z, to.z)
                )
                val end = Location(from.world,
                    max(from.x, to.x) ,
                    max(from.y, to.y) ,
                    max(from.z, to.z)
                )
                var x = start.x
                var y = start.y
                var z = start.z
                while (x < end.x || y < end.y || z < end.z) {
                    builder.location(Location(a.world, x, y, z)).spawn()
                    if (x < end.x) x += 0.5
                    if (y < end.y) y += 0.5
                    if (z < end.z) z += 0.5
                }
            }
        }

    }

    fun tick(player: Player) {
        showBorders(player, this.a, this.b, ParticleBuilder(Particle.ELECTRIC_SPARK).extra(0.0))
        showBorders(player, this.a, this.a,
            ParticleBuilder(Particle.DUST).data(Particle.DustOptions(Color.fromRGB(0, 100, 255), 1f)))
        showBorders(player, this.b, this.b,
            ParticleBuilder(Particle.DUST).data(Particle.DustOptions(Color.fromRGB(255, 100, 0), 1f)))
    }

    class FromTo(var from: Vector, var to: Vector)
}