package skywolf46.twilightsatellite.common.data.container

import skywolf46.twilightsatellite.common.data.DataContainerThreadManager
import kotlin.concurrent.write

open class ComplexDataContainer<DEPTH : DataContainer<*, *>>(
    private val threadManager: DataContainerThreadManager, provider: (DataContainerThreadManager, String) -> DEPTH
) : InMemoryDataContainer<String, DEPTH>(defaultDataProvider = { key -> provider(threadManager, key) }) {

    @Suppress("unused")
    constructor(threadManager: DataContainerThreadManager, provider: (String) -> DEPTH) : this(
        threadManager, { _, name -> provider(name) })

    override fun shutdown() {
        threadManager.shutdown()
        super.lock.write {
            super.map.forEach { (_, v) ->
                v.shutdown()
            }
        }
    }
}