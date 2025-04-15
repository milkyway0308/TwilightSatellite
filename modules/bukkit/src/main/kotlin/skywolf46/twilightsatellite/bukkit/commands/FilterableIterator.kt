package skywolf46.twilightsatellite.bukkit.commands

open class FilterableIterator<T>(protected val iterable: List<T>) : Iterator<T>, Cloneable {
    protected var index = 0

    override fun hasNext(): Boolean {
        return index < iterable.size
    }

    override fun next(): T {
        if (!hasNext())
            throw NoSuchElementException()
        return iterable[index++]
    }

    fun nextOrNull(): T? {
        return if (hasNext())
            next()
        else
            null
    }

    fun nextIf(predicate: (T) -> Boolean): T? {
        return if (hasNext()) {
            val next = next()
            if (predicate(next))
                next
            else {
                index--
                null
            }
        } else
            null
    }

    fun nextIfNot(predicate: (T) -> Boolean): T? {
        return nextIf {
            !predicate(it)
        }
    }

    fun skip(count: Int) {
        index += count
    }

    fun until(predicate: (T) -> Boolean) {
        while (hasNext()) {
            val next = next()
            if (predicate(next)) {
                index--
                break
            }
        }
    }

    fun untilNot(predicate: (T) -> Boolean) {
        until {
            !predicate(it)
        }
    }

    fun nextUntil(predicate: (T) -> Boolean): List<T> {
        return mutableListOf<T>().apply {
            until {
                predicate(it).apply {
                    if (!this) add(it)
                }
            }
        }
    }

    public override fun clone(): FilterableIterator<T> {
        return FilterableIterator(iterable).also {
            it.index = index
        }
    }

    internal fun transferFrom(from: FilterableIterator<T>) {
        index = from.index
    }
}