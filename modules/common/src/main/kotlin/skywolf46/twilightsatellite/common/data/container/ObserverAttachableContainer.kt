package skywolf46.twilightsatellite.common.data.container

interface ObserverAttachableContainer<K: Any, V: Any> {
    fun attachObserver(observer: (K, V) -> Unit)

    fun requestAcquire(key: K)

    fun getSharedObserver() : ContainerObserver<K, V>
}