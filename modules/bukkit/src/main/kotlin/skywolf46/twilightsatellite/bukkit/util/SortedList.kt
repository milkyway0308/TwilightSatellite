package skywolf46.twilightsatellite.bukkit.util

open class SortedList<T : Any>(private val comparator: Comparator<T>) : ArrayList<T>() {
    override fun add(element: T): Boolean {
        return super.add(element).apply {
            sortWith(comparator)
        }
    }

    override fun addAll(elements: Collection<T>): Boolean {
        return super.addAll(elements).apply {
            sortWith(comparator)
        }
    }

    override fun add(index: Int, element: T) {
        super.add(index, element).apply {
            sortWith(comparator)
        }
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        return super.addAll(index, elements).apply {
            sortWith(comparator)
        }
    }
}