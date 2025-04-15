package skywolf46.twilightsatellite.common.data

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class DataContainerThreadManager(period: Long, unit: TimeUnit = TimeUnit.MILLISECONDS) {
    val schedulerPool = Executors.newSingleThreadScheduledExecutor()
    val dataAccessPool = Executors.newSingleThreadExecutor()

    private val flagged = mutableSetOf<Flaggable>()

    private val lock = ReentrantReadWriteLock()

    init {
        schedulerPool.scheduleAtFixedRate({
            tickFlag()
        }, 0, period, unit)
    }

    fun addFlagTick(target: Flaggable) {
        lock.write {
            flagged.add(target)
        }
    }

    private fun tickFlag() {
        lock.read {
            flagged.forEach {
                it.onFlagTick()
            }
        }
    }

    fun submitAccess(runnable: Runnable) {
        dataAccessPool.submit(runnable)
    }

    fun attachFlag(flaggable: Flaggable) {
        lock.write {
            flagged.add(flaggable)
        }
    }

    fun shutdown() {
        schedulerPool.shutdown()
        dataAccessPool.shutdown()

        schedulerPool.awaitTermination(5, TimeUnit.SECONDS)
        dataAccessPool.awaitTermination(5, TimeUnit.SECONDS)
        lock.write {
            flagged.toList().forEach { it.forceFinalize() }
            flagged.clear()
        }
    }


}