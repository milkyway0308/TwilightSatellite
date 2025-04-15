package skywolf46.twilightsatellite.bukkit.util

import skywolf46.twilightsatellite.common.DoublePair
import skywolf46.twilightsatellite.common.IntPair
import java.util.*


class ChanceUtil {
    companion object {
        @Deprecated(
            "Feature moved to common module; Use skywolf46.twilightsatellite.common.utility package instead",
            ReplaceWith("skywolf46.twilightsatellite.common.utility.ChanceUtil"),
            DeprecationLevel.HIDDEN
        )
        val SHARED = ChanceUtil()
    }

    private val random = Random()

    fun inChance(chance: Double): Boolean {
        return random.nextDouble() * 100.0 <= chance
    }


    fun inChance(chance: Int): Boolean {
        return inChance(chance.toDouble())
    }


    fun randomIn(min: Int, max: Int): Int {
        if (min == max) return min
        if (max < min) return randomIn(max, min)
        return random.nextInt(max - min) + min
    }
}

@Deprecated(
    "Feature moved to common module; Use skywolf46.twilightsatellite.common.utility package instead",
    ReplaceWith("skywolf46.twilightsatellite.common.utility.ChanceUtil"),
    DeprecationLevel.HIDDEN
)
fun DoublePair.random(): Double {
    if (first == second) return first
    if (first > second) return second + (first - second) * Math.random()
    return first + (second - first) * Math.random()
}

@Deprecated(
    "Feature moved to common module; Use skywolf46.twilightsatellite.common.utility package instead",
    ReplaceWith("skywolf46.twilightsatellite.common.utility.ChanceUtil"),
    DeprecationLevel.HIDDEN
)
fun IntPair.random(): Int {
    if (first == second) return first
    if (first > second) return second + ((first - second) * Math.random()).toInt()
    return first + (Math.random() * (second - first)).toInt()
}

@Deprecated(
    "Feature moved to common module; Use skywolf46.twilightsatellite.common.utility package instead",
    ReplaceWith("skywolf46.twilightsatellite.common.utility.ChanceUtil"),
    DeprecationLevel.HIDDEN
)
fun DoublePair.randomInt(): Int {
    if (first.toInt() == second.toInt()) return first.toInt()
    if (first > second) return second.toInt() + (first - second).toInt()
    return first.toInt() + (Math.random() * (second - first)).toInt()
}