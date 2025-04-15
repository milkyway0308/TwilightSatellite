package skywolf46.twilightsatellite.bukkit.entrypoint

import org.koin.core.component.KoinComponent
import skywolf46.atmospherereentry.common.api.annotations.EntryPointContainer
import skywolf46.atmospherereentry.common.api.annotations.EntryPointWorker
import skywolf46.atmospherereentry.common.api.annotations.IgnoreException

@EntryPointContainer
@IgnoreException([ClassNotFoundException::class])
class BukkitEntryPoint : KoinComponent {
    init {
        Class.forName("org.bukkit.Bukkit")
    }

    @EntryPointWorker
    fun onInit() {
//        get<ClassInfoList>().filter { it.hasAnnotation(AutoArgConverter::class.java) }.forEach { classData ->
//            runCatching {
//                if (classData.constructorInfo.isEmpty())
//                    throw IllegalStateException("......Failed to load args converter ${classData.name} : Class ${classData.name} has no constructor.")
//                if (classData.constructorInfo.size > 1)
//                    throw IllegalStateException("......Failed to load args converter ${classData.name} : Class ${classData.name} has multiple constructor.")
//                if (classData.constructorInfo[0].parameterInfo.isNotEmpty())
//                    throw IllegalStateException("......Failed to load args converter ${classData.name} : Class ${classData.name} has constructor with parameter.")
//                if (!ArgumentConverter::class.java.isAssignableFrom(classData.loadClass()))
//                    throw IllegalStateException("......Failed to load args converter ${classData.name} : Class ${classData.name} is not instance of ArgumentConverter.")
//                val newInstance = classData.loadClass().newInstance()
//                val annotation = newInstance.javaClass.getAnnotation(AutoArgConverter::class.java)
//                val target =
//                    newInstance.javaClass.kotlin.supertypes.find { it.classifier is KClass<*> && it.classifier == ArgumentConverter::class }!!
//                if (annotation.specifiedTarget == Any::class) {
//                    CommandArgs.converterStorage.registerConverter(
//                        (target.arguments[1].type!!.classifier as KClass<*>).java,
//                        newInstance as ArgumentConverter<*, Any>
//                    )
//                } else {
//                    CommandArgs.converterStorage.registerConverter(
//                        annotation.specifiedTarget.java,
//                        (target.arguments[1].type!!.classifier as KClass<*>).java,
//                        newInstance as ArgumentConverter<*, Any>
//                    )
//                }
//            }.onFailure {
//                it.printStackTrace()
//            }
//        }
    }

    private fun registerCommands() {

    }
}