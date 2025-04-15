package skywolf46.twilightsatellite.common.data

interface Flaggable {
    /**
     * Called when parent flag manager ticked.
     * @return true if flag still flagged data exists, false if flag is expired.
     *          If false returned, parent flag manager will remove this from flag list.
     */
    fun onFlagTick() : Boolean

    /**
     * Force finalize flag.
     * This method will be called when parent flag manager is shutdown, or system required safe flag removal.
     */
    fun forceFinalize()

}