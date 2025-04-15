package skywolf46.twilightsatellite.common.utility

class ReadOnlyList<T : Any>(private val origin: List<T>) : List<T> by origin {

    override fun iterator(): Iterator<T> {
        return origin.iterator().asReadOnly()
    }

    override fun listIterator(): ListIterator<T> {
        return origin.listIterator().asReadOnly()
    }

    override fun listIterator(index: Int): ListIterator<T> {
        return origin.listIterator(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        return ReadOnlyList(origin.subList(fromIndex, toIndex))
    }

    override fun toString(): String {
        return "ReadOnly: ${super.toString()}"
    }

    override fun equals(other: Any?): Boolean {
        return origin.equals(other)
    }

    override fun hashCode(): Int {
        return origin.hashCode()
    }
}

fun <T : Any> List<T>.asReadOnlyList(): ReadOnlyList<T> {
    return ReadOnlyList(this)
}

fun <T : Any> Set<T>.asReadOnlyList(): ReadOnlyList<T> {
    return ReadOnlyList(this.toList())
}

