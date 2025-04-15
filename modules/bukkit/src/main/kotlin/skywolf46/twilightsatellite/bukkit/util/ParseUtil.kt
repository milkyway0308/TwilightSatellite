package skywolf46.twilightsatellite.bukkit.util

@Deprecated(
    "Feature moved to common module; Use skywolf46.twilightsatellite.common.utility package instead",
    ReplaceWith("skywolf46.twilightsatellite.common.utility.*"),
    DeprecationLevel.WARNING
)
fun String.parseRange() : Pair<Double, Double> {
    if ("~" !in this) {
        val value = this.parsePercentage()
        return value to value
    }
    val split = this.split("~")
    return (split[0].parsePercentage()) to (split[1].parsePercentage())
}

@Deprecated(
    "Feature moved to common module; Use skywolf46.twilightsatellite.common.utility package instead",
    ReplaceWith("skywolf46.twilightsatellite.common.utility.*"),
    DeprecationLevel.WARNING
)
fun String.parsePercentage() : Double {
    if ("%" !in this)
        return this.toDoubleOrNull() ?: 0.0
    return this.replace("%", "").toDoubleOrNull() ?: 0.0
}

@Deprecated(
    "Feature moved to common module; Use skywolf46.twilightsatellite.common.utility package instead",
    ReplaceWith("skywolf46.twilightsatellite.common.utility.*"),
    DeprecationLevel.WARNING
)
fun String.parseIntPercentage() : Int {
    if ("%" !in this)
        return this.toIntOrNull() ?: 0
    return this.replace("%", "").toIntOrNull() ?: 0
}

@Deprecated(
    "Feature moved to common module; Use skywolf46.twilightsatellite.common.utility package instead",
    ReplaceWith("skywolf46.twilightsatellite.common.utility.*"),
    DeprecationLevel.WARNING
)
fun String.parseIntRange() : Pair<Int, Int> {
    if ("~" !in this) {
        val value = this.parseIntPercentage()
        return value to value
    }
    val split = this.split("~")
    return (split[0].parseIntPercentage()) to (split[1].parseIntPercentage())
}