package skywolf46.twilightsatellite.bukkit.commands

class ModifiableCommandArgs<AUDIENCE : Any>(
    listenerClass: Class<*>,
    listener: AUDIENCE, input: Array<String>, index: Int
) : CommandArgs<AUDIENCE>(listenerClass, listener, input, index) {
    companion object {
        private val anyAudience = Any()

        operator fun invoke(input: Array<String>) = ModifiableCommandArgs(anyAudience, input)

        operator fun invoke(input: String) = ModifiableCommandArgs(anyAudience, input)
    }

    constructor(listener: AUDIENCE, input: Array<String>) : this(listener.javaClass, listener, input, 0)

    constructor(listener: AUDIENCE, input: String) : this(listener, input.split(" ").toTypedArray())


    fun modifyCurrent(value: String) {
        super.input[index] = value
    }

    override fun clone(): ModifiableCommandArgs<AUDIENCE> {
        return ModifiableCommandArgs(listenerClass, listener, input.clone(), index)
    }


}