package study.prikolz.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.inventory.ItemStack

object ItemUtils {
    fun text(txt: String, r: Int, g: Int, b: Int): Component {
        return Component.text(txt).color(TextColor.color(r, g, b)).decoration(TextDecoration.ITALIC, false)
    }
    fun rename(item: ItemStack, txt: String, r: Int, g: Int, b: Int) {
        val meta = item.itemMeta
        meta.displayName(text(txt, r, g, b))
        item.itemMeta = meta
    }
}