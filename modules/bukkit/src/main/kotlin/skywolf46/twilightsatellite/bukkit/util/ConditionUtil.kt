package skywolf46.twilightsatellite.bukkit.util

@Deprecated(
    "Feature moved to common module; Use skywolf46.twilightsatellite.common.utility package instead",
    ReplaceWith("skywolf46.twilightsatellite.common.utility.*"),
    DeprecationLevel.WARNING
)
fun Boolean.ifTrue(block: () -> Unit): Boolean {
    if (this) {
        block()
    }
    return this
}

@Deprecated(
    "Feature moved to common module; Use skywolf46.twilightsatellite.common.utility package instead",
    ReplaceWith("skywolf46.twilightsatellite.common.utility.*"),
    DeprecationLevel.WARNING
)
fun Boolean.ifFalse(block: () -> Unit): Boolean {
    if (!this) {
        block()
    }
    return this
}

@Deprecated(
    "Feature moved to common module; Use skywolf46.twilightsatellite.common.utility package instead",
    ReplaceWith("skywolf46.twilightsatellite.common.utility.*"),
    DeprecationLevel.WARNING
)
fun <T : Any> T?.ifNull(block: () -> Unit): T? {
    if (this == null) {
        block()
    }
    return this
}

@Deprecated(
    "Feature moved to common module; Use skywolf46.twilightsatellite.common.utility package instead",
    ReplaceWith("skywolf46.twilightsatellite.common.utility.*"),
    DeprecationLevel.WARNING
)
fun <T : Any> Boolean.mapTrue(unit: () -> T): T? {
    if (this)
        return unit()
    return null
}

@Deprecated(
    "Feature moved to common module; Use skywolf46.twilightsatellite.common.utility package instead",
    ReplaceWith("skywolf46.twilightsatellite.common.utility.*"),
    DeprecationLevel.WARNING
)
fun <T : Any> Boolean.mapFalse(unit: () -> T): T? {
    if (!this)
        return unit()
    return null
}


