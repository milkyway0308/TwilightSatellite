package skywolf46.twilightsatellite.common.utility

fun Boolean.ifTrue(block: () -> Unit): Boolean {
    if (this) {
        block()
    }
    return this
}

fun Boolean.ifFalse(block: () -> Unit): Boolean {
    if (!this) {
        block()
    }
    return this
}

fun <T : Any> T?.ifNull(block: () -> Unit): T? {
    if (this == null) {
        block()
    }
    return this
}

fun <T : Any> Boolean.mapTrue(unit: () -> T): T? {
    if (this)
        return unit()
    return null
}

fun <T : Any> Boolean.mapFalse(unit: () -> T): T? {
    if (!this)
        return unit()
    return null
}


