package skywolf46.twilightsatellite.bukkit.data

import java.util.concurrent.Executor
import java.util.concurrent.Executors

data class IOThreadGroup(
    val accessorThread: Executor = Executors.newSingleThreadExecutor(),
    val ioThread: Executor = Executors.newSingleThreadExecutor()
)