package skywolf46.twilightsatellite.common.data.container

interface ContainerObserver<K : Any, V : Any> {
    fun getValue(key: K): V?

    fun getOrRequestValue(key: K): V?

    fun getAndRequestValue(key: K): V?
}