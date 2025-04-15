package skywolf46.twilightsatellite.common.data.container

data class ContainerDataPair<K : Cloneable, V : Cloneable> (
    val key: K,
    val value: V,
    val flaggedAt: Long = System.currentTimeMillis(),
)