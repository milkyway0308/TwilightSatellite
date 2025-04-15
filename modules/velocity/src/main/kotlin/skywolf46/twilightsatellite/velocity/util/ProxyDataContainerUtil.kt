package skywolf46.twilightsatellite.velocity.util

import org.koin.mp.KoinPlatform
import skywolf46.twilightsatellite.common.data.Snapshotable
import skywolf46.twilightsatellite.common.data.container.ComplexDataContainer
import skywolf46.twilightsatellite.common.data.container.DataContainer
import skywolf46.twilightsatellite.velocity.data.container.ProxyDataListenerContainer

fun <T : Any, V : Snapshotable> DataContainer<T, V>.bindToProxy(id: String): DataContainer<T, V> {
    KoinPlatform.getKoin().get<ProxyDataListenerContainer>().bindContainer(id, this)
    return this
}

fun <T : Any, V : Snapshotable> ComplexDataContainer<DataContainer<T, V>>.bindToProxy(id: String): ComplexDataContainer<DataContainer<T, V>> {
    KoinPlatform.getKoin().get<ProxyDataListenerContainer>().bindContainer(id, this)
    return this
}