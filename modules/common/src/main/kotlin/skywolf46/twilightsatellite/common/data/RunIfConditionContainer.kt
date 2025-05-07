package skywolf46.twilightsatellite.common.data

class RunIfConditionContainer(private val condition: Boolean) {
    fun run(unit: () -> Unit) {
        if (condition) {
            unit()
        }
    }

    fun run(runnable: Runnable) {
        if (condition) {
            runnable.run()
        }
    }

    fun runFailed(condition: Boolean, unit: () -> Unit) {
        if (!condition) {
            unit()
        }
    }

    fun runFailed(condition: Boolean, runnable: Runnable) {
        if (!condition) {
            runnable.run()
        }
    }
}
