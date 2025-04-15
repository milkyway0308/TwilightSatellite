package skywolf46.twilightsatellite.bukkit.commands.matcher

import skywolf46.twilightsatellite.bukkit.commands.CommandArgs
import skywolf46.twilightsatellite.bukkit.commands.CommandMatcher

abstract class PseudoTypeWrappedMatcher : CommandMatcher {
    companion object {
        private val typeTester = mutableMapOf<String, (CommandArgs<*>) -> Boolean>()

        fun registerTester(type: String, tester: (CommandArgs<*>) -> Boolean) {
            typeTester[type] = tester
        }
    }

    fun isTypeExists(type: String): Boolean {
        return typeTester.containsKey(type)
    }

    fun isTypeSatisfied(type: String, value: CommandArgs<*>): Boolean {
        return runCatching {
            typeTester[type]?.invoke(value) ?: false
        }.getOrElse { false }
    }
}