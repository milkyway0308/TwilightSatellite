package skywolf46.twilightsatellite.bukkit.commands.matcher

import skywolf46.twilightsatellite.bukkit.commands.CommandMatcher

/**
 * Command matcher blueprint for complex arguments.
 *
 * Complex arguments are arguments that can be parsed with multiple arguments.
 * As example, "required" argument can be parsed with next example :
 *   /test <required \[test]>
 * ..or like this :
 *   /test <required>|<required <test>>
 * ComplexArgumentMatcher supports nested argument parsing like examples.
 */
open abstract class ComplexCommandMatcher : CommandMatcher {

}