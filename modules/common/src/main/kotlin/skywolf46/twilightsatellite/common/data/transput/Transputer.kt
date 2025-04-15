package skywolf46.twilightsatellite.common.data.transput

/**
 * Data I/O interface for serializers.
 *
 * Transputer manage synchronous or asynchronous data export and load.
 *
 * Implementations:
 *  - [SyncFileTransputer]: Synchronized Key file based byte array transputer.
 *  - [MultiLayerFileTransputer]: Key file based byte array transputer with multiple layer backup system.
 */
interface Transputer<DATA: Any> {
    companion object

    /**
     * Export data to key.
     * @param key Key to export data.
     * @param data Data to export.
     */
    fun export(key: String, data: DATA)

    /**
     * Import data from key.
     * @param key Key to import data.
     * @param data Data loader.
     */
    fun import(key: String, data: (DATA?) -> Unit)

    /**
     * Delete data from key.
     * @param key Key to delete data.
     */
    fun delete(key: String)

    /**
     * Cleanup transputer, release all resources. Shutdown transputer gracefully.
     * This action cannot be performed twice.
     *
     * If transputer is async, will hang until all async operation is completed.
     */
    fun cleanUp() : Boolean

    /**
     * Check if transputer is async.
     */
    fun isAsync() : Boolean
}

