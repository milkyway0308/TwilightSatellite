package skywolf46.twilightsatellite.common.data.container

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class AttachableContainerObserver<K : Any, V : Any>(private val origin: ObserverAttachableContainer<K, V>) : ContainerObserver<K, V>{

    private val map = mutableMapOf<K, V>()

    private val lock = ReentrantReadWriteLock()

    init {
        origin.attachObserver { k, v ->
            lock.write { map[k] = v }
        }
    }

    override fun getValue(key: K): V? {
        return lock.read {
            map[key]
        }
    }


    override fun getOrRequestValue(key: K): V? {
        return lock.read { map[key] } ?: kotlin.run {
            // Observer will automatically acquire data from origin
            origin.requestAcquire(key)
            null
        }
    }


    override fun getAndRequestValue(key: K): V? {
        return lock.read { map[key] }.apply {
            origin.requestAcquire(key)
        }
    }
}