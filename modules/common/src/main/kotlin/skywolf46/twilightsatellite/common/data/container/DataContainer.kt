package skywolf46.twilightsatellite.common.data.container

import kotlinx.coroutines.*
import java.util.concurrent.ArrayBlockingQueue

/**
 * DataContainer is a container that contains data with key-value pair.
 *
 * It's similar to Map, but it's designed to support thread-safe, async, and auto-save feature.
 *
 */
interface DataContainer<K : Any, V : Any> {
    companion object

    fun acquire(key: K, accessor: (V) -> Unit): DataContainer<K, V>

    fun modify(key: K, modifier: (V) -> V): DataContainer<K, V>

    fun detailedModify(key: K, modifier: (V) -> ModificationOperation<V>): DataContainer<K, V>

    fun update(key: K, value: V): DataContainer<K, V>

    fun contains(key: K, accessor: (Boolean) -> Unit): DataContainer<K, V>

    fun remove(key: K): DataContainer<K, V>

    fun flag(key: K): DataContainer<K, V>

    fun deflag(key: K): DataContainer<K, V>

    fun shutdown() {

    }

    fun listKeys(accessor: (List<K>) -> Unit): DataContainer<K, V> {
        throw UnsupportedOperationException("listKeys() is not supported in this container.")
    }

    fun listValues(accessor: (List<V>) -> Unit): DataContainer<K, V> {
        throw UnsupportedOperationException("listValues() is not supported in this container.")
    }

    fun entries(accessor: (Map<K, V>) -> Unit): DataContainer<K, V> {
        throw UnsupportedOperationException("entries() is not supported in this container.")
    }

    fun supportSave(): Boolean

    fun flushSave()

    fun modifyOrigin(key: K, modifier: (V) -> Unit): DataContainer<K, V> {
        return modify(key) {
            modifier(it)
            it
        }
    }

    fun prepareChain(key: K): DataContainerChainPrepare<K, V> {
        return DataContainerChainPrepare(key, this)
    }

    class DataContainerChainPrepare<K : Any, V : Any>(private val key: K, private val container: DataContainer<K, V>) {
        companion object {
            private var chainScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        }

        private val checkOperation = mutableListOf<DataOperationPair<V>>()

        private val operations = mutableListOf<Pair<DataContainerAction, (V) -> ModificationOperation<V>>>()

        private val completeAction = mutableListOf<() -> Unit>()

        private val failedAction = mutableListOf<() -> Unit>()

        private val finalizeAction = mutableListOf<() -> Unit>()

        fun continueIf(predicate: (V) -> Boolean): DataContainerChainPrepare<K, V> {
            operations += DataContainerAction.READ_VERIFY to {
                if (predicate(it)) ModificationOperation.empty()
                else ModificationOperation.noModify()
            }
            return this
        }

        inline fun stopIf(crossinline predicate: (V) -> Boolean): DataContainerChainPrepare<K, V> {
            return continueIf { !predicate(it) }
        }

        fun withValidate(predicate: (V) -> Boolean): DataContainerChainPrepare<K, V> {
            checkOperation += DataOperationPair(predicate, null)
            return this
        }

        /**
         * Run if operation end successfully.
         */
        fun afterComplete(accessor: () -> Unit): DataContainerChainPrepare<K, V> {
            completeAction += accessor
            return this
        }

        /**
         * Run if operation end successfully.
         */
        fun afterFailed(accessor: () -> Unit): DataContainerChainPrepare<K, V> {
            failedAction += accessor
            return this
        }

        /**
         * Run anyway when operation finished.
         */
        fun finalize(accessor: () -> Unit): DataContainerChainPrepare<K, V> {
            finalizeAction += accessor
            return this
        }

        fun runIfFailed(accessor: (V) -> Unit): DataContainerChainPrepare<K, V> {
            checkOperation[checkOperation.size - 1].onFailed = accessor
            return this
        }

        fun acquire(accessor: (V) -> Unit): DataContainerChainPrepare<K, V> {
            operations += DataContainerAction.READ to {
                accessor(it)
                ModificationOperation.empty()
            }
            return this
        }

        /**
         * Modify data without validation.
         *
         * This operation can only edit data.
         */
        fun modifyCertainly(modifier: (V) -> V): DataContainerChainPrepare<K, V> {
            operations += DataContainerAction.WRITE to {
                ModificationOperation.modify(modifier(it))
            }
            return this
        }

        /**
         * Modify data with validation.
         *
         * This operation can only edit data.
         */
        fun modifyData(modifier: (V) -> ModificationOperation<V>): DataContainerChainPrepare<K, V> {
            operations += DataContainerAction.WRITE_WITH_VALIDATE to {
                modifier(it)
            }
            return this
        }

        /**
         * Modify data with validation.
         *
         * This operation can remove, edit data.
         */
        fun modify(modifier: (V) -> ModificationOperation<V>): DataContainerChainPrepare<K, V> {
            operations += DataContainerAction.WRITE_WITH_VALIDATE to {
                modifier(it)
            }
            return this
        }

        fun start(): DataContainer<K, V> {
            chainScope.launch {
                val channel = ArrayBlockingQueue<DataContainerResult>(1)
                var isStopped = false
                for (x in operations) {
                    if (!launchOperation(channel, x)) {
                        isStopped = true
                        break
                    }
                }
                for (action in if (isStopped) failedAction else completeAction) action()
                for (action in finalizeAction) action()
                channel.clear()
            }
            return container
        }


        private suspend fun launchOperation(
            channel: ArrayBlockingQueue<DataContainerResult>,
            operation: Pair<DataContainerAction, (V) -> ModificationOperation<V>>
        ): Boolean {
            when (operation.first) {
                DataContainerAction.READ_VERIFY -> {
                    container.acquire(key) {
                        if (operation.second(it) == ModificationOperation.EMPTY) {
                            chainScope.launch { channel.put(DataContainerResult.SUCCESS) }
                        } else {
                            chainScope.launch { channel.put(DataContainerResult.STOP_CHAIN) }
                        }
                    }
                }

                DataContainerAction.READ -> {
                    container.acquire(key) {
                        operation.second(it).apply {
                            chainScope.launch { channel.put(DataContainerResult.SUCCESS) }
                        }
                    }
                }

                DataContainerAction.WRITE -> {
                    container.modify(key) {
                        operation.second(it).data.apply {
                            chainScope.launch { channel.put(DataContainerResult.SUCCESS) }
                        }
                    }
                }

                DataContainerAction.WRITE_WITH_VALIDATE -> {
                    container.detailedModify(key) {
                        for (x in checkOperation) {
                            if (!x.check(it)) {
                                x.onFailed?.invoke(it)
                                chainScope.launch { channel.put(DataContainerResult.STOP_CHAIN) }
                                return@detailedModify ModificationOperation.noModify()
                            }
                        }
                        operation.second(it).apply {
                            chainScope.launch { channel.put(DataContainerResult.SUCCESS) }
                        }
                    }
                }
            }
            return withContext(Dispatchers.IO) {
                channel.take()
            } == DataContainerResult.SUCCESS
        }

        private class DataOperationPair<V>(
            val check: (V) -> Boolean, var onFailed: ((V) -> Unit)?
        )

        private enum class DataContainerResult {
            STOP_CHAIN, SUCCESS
        }

        private enum class DataContainerAction {
            READ, READ_VERIFY, WRITE, WRITE_WITH_VALIDATE
        }
    }

    class ModificationOperation<V>(val data: V) {
        companion object {
            val NO_MODIFY = ModificationOperation(null)

            val REMOVE = ModificationOperation(null)

            val EMPTY = ModificationOperation(null)

            fun <V : Any> noModify() = NO_MODIFY as ModificationOperation<V>

            fun <V : Any> remove() = REMOVE as ModificationOperation<V>

            fun <V : Any> empty() = EMPTY as ModificationOperation<V>

            fun <V : Any> modify(data: V) = ModificationOperation(data)
        }
    }
}