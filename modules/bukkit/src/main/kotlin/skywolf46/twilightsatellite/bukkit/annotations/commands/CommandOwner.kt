package skywolf46.twilightsatellite.bukkit.annotations.commands

import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.KClass

annotation class CommandOwner(val owner: KClass<out Any>)