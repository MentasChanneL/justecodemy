package study.prikolz.command

import com.mojang.brigadier.Message
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

object PlayerArgumentType : CustomArgumentType<Player, String> {

    private val stringParser = StringArgumentType.string()

    override fun parse(p0: StringReader): Player {
        return Bukkit.getPlayer( this.stringParser.parse(p0) )?: throw DynamicCommandExceptionType { Message { "Player not found!" } }.create ( this.stringParser.parse(p0) )
    }

    override fun getNativeType(): ArgumentType<String> {
        return stringParser
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        for(player in Bukkit.getOnlinePlayers()) builder.suggest(player.name)
        return builder.buildFuture()
    }

    fun getPlayer(context: CommandContext<*>, name: String?): Player {
        return context.getArgument(name, Player::class.java) as Player
    }

}