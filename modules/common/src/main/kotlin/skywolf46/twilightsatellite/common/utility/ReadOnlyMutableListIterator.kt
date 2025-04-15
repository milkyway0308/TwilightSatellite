package skywolf46.twilightsatellite.common.utility

class ReadOnlyMutableListIterator<T : Any>(private val origin: MutableListIterator<T>) : MutableListIterator<T> by origin {
    override fun remove() {
        throw UnsupportedOperationException("This iterator is read-only.")
    }

    override fun add(element: T) {
        throw UnsupportedOperationException("This iterator is read-only.")
    }

    override fun set(element: T) {
        throw UnsupportedOperationException("This iterator is read-only.")
    }
}

fun <T : Any> ListIterator<T>.asReadOnly(): ListIterator<T> {
    if (this is MutableListIterator) return ReadOnlyMutableListIterator(this)
    return this
}
