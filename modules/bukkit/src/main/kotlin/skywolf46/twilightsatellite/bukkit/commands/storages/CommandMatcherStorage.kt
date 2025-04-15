package skywolf46.twilightsatellite.bukkit.commands.storages

import arrow.core.None
import arrow.core.Option
import arrow.core.toOption
import skywolf46.twilightsatellite.bukkit.commands.CommandMatcher
import skywolf46.twilightsatellite.bukkit.commands.FilterableIterator
import skywolf46.twilightsatellite.bukkit.util.SortedList

class CommandMatcherStorage : SortedList<CommandMatcher>(Comparator.comparingInt { it.getPriority() }) {
    fun parseMatched(args: FilterableIterator<Char>): Option<CommandMatcher> {
        return mapNotNull {
            val clonedArg = args.clone()
            it.createFrom(clonedArg).fold({ null }, { parsedMatcher ->
                args.transferFrom(clonedArg)
                parsedMatcher
            })
        }.let { if (isEmpty()) None else it.first().toOption() }
    }

    fun parseAllMatched(args: FilterableIterator<Char>): Option<List<CommandMatcher>> {
        val list = mutableListOf<CommandMatcher>()
        while (args.hasNext()) {
            any {
                val clonedArg = args.clone()
                it.createFrom(clonedArg).tap { parsedMatcher ->
                    list.add(parsedMatcher)
                    args.transferFrom(clonedArg)
                }.isDefined()
            }
        }
        return list.toOption()
    }
}