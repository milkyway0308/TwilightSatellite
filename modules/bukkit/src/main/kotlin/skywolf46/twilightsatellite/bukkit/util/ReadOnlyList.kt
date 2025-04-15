package skywolf46.twilightsatellite.bukkit.util

class ReadOnlyList<T: Any>(private val origin: List<T>) : List<T>{
    override val size: Int
        get() = origin.size

    override fun get(index: Int): T {
        return origin[index]
    }

    override fun isEmpty(): Boolean {
        return origin.isEmpty()
    }

    override fun iterator(): Iterator<T> {
        return origin.iterator()
    }

    override fun listIterator(): ListIterator<T> {
        return origin.listIterator()
    }

    override fun listIterator(index: Int): ListIterator<T> {
        return origin.listIterator(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        return origin.subList(fromIndex, toIndex)
    }

    override fun lastIndexOf(element: T): Int {
        return origin.lastIndexOf(element)
    }

    override fun indexOf(element: T): Int {
        return origin.indexOf(element)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return origin.containsAll(elements)
    }

    override fun contains(element: T): Boolean {
        return origin.contains(element)
    }


}