package skywolf46.twilightsatellite.bukkit.event

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import skywolf46.twilightsatellite.bukkit.commands.CommandMatcher
import skywolf46.twilightsatellite.bukkit.util.ReadOnlyList
import kotlin.reflect.KClass


class CommandSuggestionEvent(
    val context: CommandContext<Any>,
    val matcher: CommandMatcher,
    val commandOwner: KClass<out Any>,
    val builder: SuggestionsBuilder,
    val isAsync: Boolean
) : Event(isAsync) {
    companion object {
        val handlerList = HandlerList()
            @JvmStatic get
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    val commands = ReadOnlyList(builder.input.split(" "))

    fun isOwner(owner: KClass<out Any>): Boolean {
        return owner.java.isAssignableFrom(commandOwner.java)
    }

    fun suggestWithMatchingLastArg(iterable: Iterator<String>) {
        val last = builder.input.split(" ").last()
        if (last.isBlank()) {
            iterable.forEach { builder.suggest(it) }
        } else {
            iterable.forEach {
                if (it.startsWith(last)) {
                    builder.suggest(it)
                }
            }
        }
    }

    /**
     * Returns command parameter list size.
     */
    fun argLength(): Int {
        return commands.size
    }

    /**
     * Returns last argument of command.
     */
    fun lastArg(): String {
        return builder.input.split(" ").last()
    }

    /**
     * Suggests command to player.
     *
     * @param autoFilter If true, filter suggestion by last argument.
     * @param unit Suggestion provider.
     */
    fun suggest(autoFilter: Boolean, unit: (CommandSuggestionEvent) -> List<String>) {
        if (autoFilter) {
            suggestWithMatchingLastArg(unit(this).iterator())
        } else {
            unit(this).forEach {
                builder.suggest(it)
            }
        }
    }

    /**
     * Suggests command to player.
     *
     * @param owner Command owner instance target.
     * @param autoFilter If true, filter suggestion by last argument.
     * @param unit Suggestion provider.
     */
    fun suggestAt(owner: KClass<out Any>, autoFilter: Boolean, unit: (CommandSuggestionEvent) -> List<String>) {
        if (isOwner(owner)) {
            suggest(autoFilter, unit)
        }
    }

    /**
     * Suggests command to player with auto filtering.
     *
     * @param owner Command owner instance target.
     * @param unit Suggestion provider.
     */
    fun suggestAt(owner: KClass<out Any>, unit: (CommandSuggestionEvent) -> List<String>) {
        suggestAt(owner, true, unit)
    }

    /**
     * Suggests command to player.
     *
     * @param owner Command owner instance target.
     * @param target Command target matcher name that marked at command with exclamatory mark.
     * @param autoFilter If true, filter suggestion by last argument.
     * @param unit Suggestion provider.
     */
    fun suggestAt(
        owner: KClass<out Any>, target: String, autoFilter: Boolean, unit: (CommandSuggestionEvent) -> List<String>
    ) {
        if (isOwner(owner) && matcher.getCommandName() == target) {
            suggest(autoFilter, unit)
        }
    }

    /**
     * Suggests command to player with auto filtering.
     *
     * @param owner Command owner instance target.
     * @param target Command target matcher name that marked at command with exclamatory mark.
     * @param unit Suggestion provider.
     */
    fun suggestAt(owner: KClass<out Any>, target: String, unit: (CommandSuggestionEvent) -> List<String>) {
        suggestAt(owner, target, true, unit)
    }


    /**
     * Suggests command to player.
     *
     * @param T Command owner instance target.
     * @param autoFilter If true, filter suggestion by last argument.
     * @param unit Suggestion provider.
     */
    inline fun <reified T : Any> suggestAt(
        autoFilter: Boolean, noinline unit: (CommandSuggestionEvent) -> List<String>
    ) {
        suggestAt(T::class, autoFilter, unit)
    }

    /**
     * Suggests command to player with auto filtering.
     *
     * @param T Command owner instance target.
     * @param unit Suggestion provider.
     */
    inline fun <reified T : Any> suggestAt(noinline unit: (CommandSuggestionEvent) -> List<String>) {
        suggestAt(T::class, unit)
    }

    /**
     * Suggests command to player.
     *
     * @param T Command owner instance target.
     * @param target Command target matcher name that marked at command with exclamatory mark.
     * @param autoFilter If true, filter suggestion by last argument.
     * @param unit Suggestion provider.
     */
    inline fun <reified T : Any> suggestAt(
        target: String, autoFilter: Boolean, noinline unit: (CommandSuggestionEvent) -> List<String>
    ) {
        suggestAt(T::class, target, autoFilter, unit)
    }

    /**
     * Suggests command to player with auto filtering.
     *
     * @param T Command owner instance target.
     * @param target Command target matcher name that marked at command with exclamatory mark.
     * @param unit Suggestion provider.
     */
    inline fun <reified T : Any> suggestAt(target: String, noinline unit: (CommandSuggestionEvent) -> List<String>) {
        suggestAt(T::class, target, unit)
    }
}