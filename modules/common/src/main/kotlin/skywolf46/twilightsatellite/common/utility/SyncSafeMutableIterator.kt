package skywolf46.twilightsatellite.common.utility

/**
 * 동기 환경에서 데이터 추가가 일어나도 항상 주어진 횟수까지 오류 없이 안전하게 반복하는 이터레이터입니다.
 * 비동기 환경에서는 작동을 보장할 수 없습니다.
 */
class SyncSafeMutableIterator<T : Any>(private val origin: MutableList<T>) : MutableIterator<T> {
    var movedCount = 0
    var index = 0
    var targetIndex = origin.size

    override fun hasNext(): Boolean {
        return index < targetIndex && origin.size > index
    }

    fun movedCount(): Int {
        return movedCount
    }

    override fun next(): T {
        if (!hasNext())
            throw NoSuchElementException("No more element in iterator.")
        movedCount++
        return origin[index++]
    }

    override fun remove() {
        origin.removeAt(--index)
        targetIndex--
    }

    fun add(new: T) {
        origin.add(new)
    }

    fun replace(new: T) {
        origin[index - 1] = new
    }
}

fun MutableList<String>.safeIterator(): SyncSafeMutableIterator<String> {
    return SyncSafeMutableIterator(this)
}