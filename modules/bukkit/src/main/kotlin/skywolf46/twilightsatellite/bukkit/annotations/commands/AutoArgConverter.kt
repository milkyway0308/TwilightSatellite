package skywolf46.twilightsatellite.bukkit.annotations.commands

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AutoArgConverter(val specifiedTarget: KClass<out Any> = Any::class)