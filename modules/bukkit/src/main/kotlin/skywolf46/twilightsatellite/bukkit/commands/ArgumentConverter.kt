package skywolf46.twilightsatellite.bukkit.commands

interface ArgumentConverter<AUDIENCE : Any, T : Any> {
    fun convert(arg: CommandArgs<AUDIENCE>): T

    fun name(): String
}