package skywolf46.twilightsatellite.bukkit.commands

interface CommandArgTransformer<FROM : Any, TO : Any> {
    fun convert(from: CommandArgs<FROM>): CommandArgs<TO>?

    open fun convertSilent(from: CommandArgs<FROM>): CommandArgs<TO>? {
        return convert(from)
    }
}