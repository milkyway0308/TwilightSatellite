package skywolf46.twilightsatellite.common.utility

class ReadOnlyMutableIterator<T : Any>(private val origin: MutableIterator<T>) : MutableIterator<T> by origin {
    override fun remove() {
        throw UnsupportedOperationException("This iterator is read-only.")
    }
}

fun <T : Any> Iterator<T>.asReadOnly(): Iterator<T> {
    if (this is MutableIterator) return ReadOnlyMutableIterator(this)
    return this
}
