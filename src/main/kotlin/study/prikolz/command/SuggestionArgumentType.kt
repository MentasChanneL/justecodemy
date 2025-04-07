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
import java.util.concurrent.CompletableFuture

class SuggestionArgumentType(list: MutableList<String>) : CustomArgumentType<String, String> {

    private val suggestions = list
    private val stringParser = StringArgumentType.string()

    override fun parse(p0: StringReader): String {
        val parseResult = this.stringParser.parse(p0)
        if (!suggestions.contains(parseResult)) throw DynamicCommandExceptionType { Message { "Unknown parameter!" } }.create ( parseResult )
        return parseResult
    }

    override fun getNativeType(): ArgumentType<String> {
        return stringParser
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        for(s in suggestions) builder.suggest(s)
        return builder.buildFuture()
    }

}