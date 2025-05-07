package skywolf46.twilightsatellite.bukkit.commands

import arrow.core.*
import skywolf46.twilightsatellite.bukkit.commands.exceptions.ArgumentNotEnoughException
import skywolf46.twilightsatellite.bukkit.commands.exceptions.ConditionMismatchedException
import skywolf46.twilightsatellite.bukkit.commands.storages.ArgumentConverterStorage
import skywolf46.twilightsatellite.common.data.RunIfConditionContainer
import skywolf46.twilightsatellite.common.data.collections.*
import skywolf46.twilightsatellite.common.utility.ifFalse
import java.util.function.Consumer
import kotlin.reflect.KClass

open class CommandArgs<AUDIENCE : Any>(
    val listenerClass: Class<*>, val listener: AUDIENCE, protected var input: Array<String>, protected var index: Int
) : Cloneable {
    companion object {
        private val anyAudience = Any()
        val converterStorage = ArgumentConverterStorage()

        @JvmName("create")
        @JvmStatic
        operator fun invoke(input: Array<String>) = CommandArgs(anyAudience, input)

        @JvmStatic
        @JvmName("create")
        operator fun invoke(input: String) = CommandArgs(anyAudience, input)
    }

    constructor(listener: AUDIENCE, input: Array<String>, index: Int = 0) : this(
        listener.javaClass, listener, input, index
    )

    constructor(listener: AUDIENCE, input: String, index: Int = 0) : this(
        listener, input.split(" ").toTypedArray(), index
    )

    private var preArguments = mutableListOf<Any>()

    private var preArgumentIndex = 0

    val commandBase = input[0]


    fun hasNext(): Boolean {
        return index < input.size
    }

    fun addPreArgument(arg: Any) {
        preArguments.add(arg)
    }


    fun next(): String {
        if (!hasNext()) throw ArgumentNotEnoughException("No more arguments")
        return input[index++]
    }

    fun peek(): String {
        if (!hasNext()) throw ArgumentNotEnoughException("No more arguments")
        return input[index]
    }

    fun nextAll(amount: Int): String {
        val builder = StringBuilder()
        for (x in index until (index + amount).coerceAtMost(input.size)) {
            builder.append(input[x]).append(" ")
        }
        if (builder.isNotEmpty()) builder.deleteCharAt(builder.length - 1)
        return builder.toString()
    }

    fun nextAll(): String {
        return nextAll(input.size - index)
    }

    fun leftSize(): Int {
        return input.size - index
    }

    open fun copyDataFrom(args: CommandArgs<*>) {
        index = args.index
        input = args.input
        preArguments = args.preArguments
        preArgumentIndex = args.preArgumentIndex
    }

    inline fun <T : Any> convert(cls: Class<T>, unit: CommandArgs<AUDIENCE>.(T) -> Unit): ConvertResult {
        return convert(cls).map {
            unit(it.first, it.second)
            ConvertResult(true, null)
        }.getOrElse { it }
    }

    fun <T : Any> convert(cls: Class<T>): Either<ConvertResult, Pair<CommandArgs<AUDIENCE>, T>> {
        return try {
            val next = this.clone()
            val converted = ((converterStorage.getConverter(cls) ?: converterStorage.getConverter(
                listenerClass, cls
            )) as? ArgumentConverter<AUDIENCE, Any>)?.convert(next) ?: throw IllegalStateException("No converter found")
            (next to (converted as T)).right()
        } catch (e: Throwable) {
            ConvertResult(false, e).left()
        }
    }

    fun <T : Any> consume(cls: Class<T>): Either<ConvertResult, T> {
        return try {
            val converted = ((converterStorage.getConverter(cls) ?: converterStorage.getConverter(
                listenerClass, cls
            )) as? ArgumentConverter<AUDIENCE, Any>)?.convert(this) ?: throw IllegalStateException("No converter found")
            (converted as T).right()
        } catch (e: Throwable) {
            ConvertResult(false, e).left()
        }
    }

    /**
     * Precondition sector
     */

    fun any(vararg condition: Precondition<out AUDIENCE>): CommandArgs<AUDIENCE> {
        return any(false, *condition)
    }

    fun none(vararg condition: Precondition<out AUDIENCE>): CommandArgs<AUDIENCE> {
        return none(false, *condition)
    }

    fun all(vararg condition: Precondition<out AUDIENCE>): CommandArgs<AUDIENCE> {
        return all(false, *condition)
    }

    fun any(silent: Boolean, vararg condition: Precondition<out AUDIENCE>): CommandArgs<AUDIENCE> {
        if (silent) {
            condition.any { (it as Precondition<AUDIENCE>).checkSilent(this) }.ifFalse {
                throw ConditionMismatchedException()
            }
            return this
        }
        condition.any { (it as Precondition<AUDIENCE>).check(this) }.ifFalse {
            throw ConditionMismatchedException()
        }
        return this
    }

    fun none(silent: Boolean, vararg condition: Precondition<out AUDIENCE>): CommandArgs<AUDIENCE> {
        if (silent) {
            condition.none { (it as Precondition<AUDIENCE>).checkSilent(this) }.ifFalse {
                throw ConditionMismatchedException()
            }
            return this
        }
        condition.none { (it as Precondition<AUDIENCE>).check(this) }.ifFalse {
            throw ConditionMismatchedException()
        }
        return this
    }

    fun all(silent: Boolean, vararg condition: Precondition<out AUDIENCE>): CommandArgs<AUDIENCE> {
        if (silent) {
            condition.all { (it as Precondition<AUDIENCE>).checkSilent(this) }.ifFalse {
                throw ConditionMismatchedException()
            }
            return this
        }
        condition.all { (it as Precondition<AUDIENCE>).check(this) }.ifFalse {
            throw ConditionMismatchedException()
        }
        return this
    }

    /**
     * Listener transformation Sector
     */

    inline fun <T : Any> transform(
        transformer: CommandArgTransformer<AUDIENCE, T>, consumer: CommandArgs<T>.() -> Unit
    ) {
        val result = transformer.convert(this) ?: return
        consumer(result)
    }

    inline fun <T : Any> transformSilent(
        transformer: CommandArgTransformer<AUDIENCE, T>, consumer: CommandArgs<T>.() -> Unit
    ) {
        val result = transformer.convertSilent(this) ?: return
        consumer(result)
    }

    fun transformCheck(
        transformer: CommandArgTransformer<AUDIENCE, *>,
        consumer: CommandArgs<*>.() -> Unit
    ) : RunIfConditionContainer {
        val result = transformer.convert(this) ?: return RunIfConditionContainer(false)
        consumer(result)
        return RunIfConditionContainer(true)
    }
    /**
     * Argument processing function sector
     */

    fun <T : Any> nextArg(cls: KClass<out T>): Either<ConvertResult, Pair<CommandArgs<AUDIENCE>, T>> {
        return if (preArguments.size <= preArgumentIndex) {
            convert(cls.java as Class<T>)
        } else {
            runCatching {
                val clonedArg = this.clone()
                val converted = preArguments[clonedArg.preArgumentIndex++] as T
                (clonedArg to converted).right()
            }.getOrElse {
                it.printStackTrace()
                ConvertResult(false, it).left()
            }
        }
    }

    fun <T : Any> consumeNextArg(cls: KClass<out T>): Either<ConvertResult, T> {
        return if (preArguments.size <= preArgumentIndex) {
            consume(cls.java as Class<T>)
        } else {
            runCatching {
                val converted = preArguments[preArgumentIndex++] as T
                converted.right()
            }.getOrElse {
                it.printStackTrace()
                ConvertResult(false, it).left()
            }
        }
    }


    inline fun <reified T : Any> nextArg(unit: CommandArgs<AUDIENCE>.(T) -> Unit): Option<ConvertResult> {
        return nextArg(T::class).onRight {
            unit(it.first, it.second)
        }.map { null }.getOrElse { it }.toOption()
    }

    @Suppress("DuplicatedCode")
    inline fun <reified T1 : Any, reified T2 : Any> nextArg(unit: CommandArgs<AUDIENCE>.(T1, T2) -> Unit): Option<ConvertResult> {
        val sharedArg = this.clone()
        val t1 = sharedArg.consumeNextArg(T1::class).getOrElse { return it.toOption() }
        val t2 = sharedArg.consumeNextArg(T2::class).getOrElse { return it.toOption() }
        unit(sharedArg, t1, t2)
        return None
    }

    @Suppress("DuplicatedCode")
    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any> nextArg(unit: CommandArgs<AUDIENCE>.(T1, T2, T3) -> Unit): Option<ConvertResult> {
        val sharedArg = this.clone()
        val t1 = sharedArg.consumeNextArg(T1::class).getOrElse { return it.toOption() }
        val t2 = sharedArg.consumeNextArg(T2::class).getOrElse { return it.toOption() }
        val t3 = sharedArg.consumeNextArg(T3::class).getOrElse { return it.toOption() }
        unit(sharedArg, t1, t2, t3)
        return None
    }

    @Suppress("DuplicatedCode")
    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any> nextArg(unit: CommandArgs<AUDIENCE>.(T1, T2, T3, T4) -> Unit): Option<ConvertResult> {
        val sharedArg = this.clone()
        val t1 = sharedArg.consumeNextArg(T1::class).getOrElse { return it.toOption() }
        val t2 = sharedArg.consumeNextArg(T2::class).getOrElse { return it.toOption() }
        val t3 = sharedArg.consumeNextArg(T3::class).getOrElse { return it.toOption() }
        val t4 = sharedArg.consumeNextArg(T4::class).getOrElse { return it.toOption() }
        unit(sharedArg, t1, t2, t3, t4)
        return None
    }

    @Suppress("DuplicatedCode")
    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any> nextArg(unit: CommandArgs<AUDIENCE>.(T1, T2, T3, T4, T5) -> Unit): Option<ConvertResult> {
        val sharedArg = this.clone()
        val t1 = sharedArg.consumeNextArg(T1::class).getOrElse { return it.toOption() }
        val t2 = sharedArg.consumeNextArg(T2::class).getOrElse { return it.toOption() }
        val t3 = sharedArg.consumeNextArg(T3::class).getOrElse { return it.toOption() }
        val t4 = sharedArg.consumeNextArg(T4::class).getOrElse { return it.toOption() }
        val t5 = sharedArg.consumeNextArg(T5::class).getOrElse { return it.toOption() }
        unit(sharedArg, t1, t2, t3, t4, t5)
        return None
    }

    @Suppress("DuplicatedCode")
    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any> nextArg(
        unit: CommandArgs<AUDIENCE>.(T1, T2, T3, T4, T5, T6) -> Unit
    ): Option<ConvertResult> {
        val sharedArg = this.clone()
        val t1 = sharedArg.consumeNextArg(T1::class).getOrElse { return it.toOption() }
        val t2 = sharedArg.consumeNextArg(T2::class).getOrElse { return it.toOption() }
        val t3 = sharedArg.consumeNextArg(T3::class).getOrElse { return it.toOption() }
        val t4 = sharedArg.consumeNextArg(T4::class).getOrElse { return it.toOption() }
        val t5 = sharedArg.consumeNextArg(T5::class).getOrElse { return it.toOption() }
        val t6 = sharedArg.consumeNextArg(T6::class).getOrElse { return it.toOption() }
        unit(sharedArg, t1, t2, t3, t4, t5, t6)
        return None
    }

    @Suppress("DuplicatedCode")
    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any> nextArg(
        unit: CommandArgs<AUDIENCE>.(T1, T2, T3, T4, T5, T6, T7) -> Unit
    ): Option<ConvertResult> {
        val sharedArg = this.clone()
        val t1 = sharedArg.consumeNextArg(T1::class).getOrElse { return it.toOption() }
        val t2 = sharedArg.consumeNextArg(T2::class).getOrElse { return it.toOption() }
        val t3 = sharedArg.consumeNextArg(T3::class).getOrElse { return it.toOption() }
        val t4 = sharedArg.consumeNextArg(T4::class).getOrElse { return it.toOption() }
        val t5 = sharedArg.consumeNextArg(T5::class).getOrElse { return it.toOption() }
        val t6 = sharedArg.consumeNextArg(T6::class).getOrElse { return it.toOption() }
        val t7 = sharedArg.consumeNextArg(T7::class).getOrElse { return it.toOption() }
        unit(sharedArg, t1, t2, t3, t4, t5, t6, t7)
        return None
    }

    @Suppress("DuplicatedCode")
    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any> nextArg(
        unit: CommandArgs<AUDIENCE>.(T1, T2, T3, T4, T5, T6, T7, T8) -> Unit
    ): Option<ConvertResult> {
        val sharedArg = this.clone()
        val t1 = sharedArg.consumeNextArg(T1::class).getOrElse { return it.toOption() }
        val t2 = sharedArg.consumeNextArg(T2::class).getOrElse { return it.toOption() }
        val t3 = sharedArg.consumeNextArg(T3::class).getOrElse { return it.toOption() }
        val t4 = sharedArg.consumeNextArg(T4::class).getOrElse { return it.toOption() }
        val t5 = sharedArg.consumeNextArg(T5::class).getOrElse { return it.toOption() }
        val t6 = sharedArg.consumeNextArg(T6::class).getOrElse { return it.toOption() }
        val t7 = sharedArg.consumeNextArg(T7::class).getOrElse { return it.toOption() }
        val t8 = sharedArg.consumeNextArg(T8::class).getOrElse { return it.toOption() }
        unit(sharedArg, t1, t2, t3, t4, t5, t6, t7, t8)
        return None
    }

    @Suppress("DuplicatedCode")
    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified T9 : Any> nextArg(
        unit: CommandArgs<AUDIENCE>.(T1, T2, T3, T4, T5, T6, T7, T8, T9) -> Unit
    ): Option<ConvertResult> {
        val sharedArg = this.clone()
        val t1 = sharedArg.consumeNextArg(T1::class).getOrElse { return it.toOption() }
        val t2 = sharedArg.consumeNextArg(T2::class).getOrElse { return it.toOption() }
        val t3 = sharedArg.consumeNextArg(T3::class).getOrElse { return it.toOption() }
        val t4 = sharedArg.consumeNextArg(T4::class).getOrElse { return it.toOption() }
        val t5 = sharedArg.consumeNextArg(T5::class).getOrElse { return it.toOption() }
        val t6 = sharedArg.consumeNextArg(T6::class).getOrElse { return it.toOption() }
        val t7 = sharedArg.consumeNextArg(T7::class).getOrElse { return it.toOption() }
        val t8 = sharedArg.consumeNextArg(T8::class).getOrElse { return it.toOption() }
        val t9 = sharedArg.consumeNextArg(T9::class).getOrElse { return it.toOption() }
        unit(sharedArg, t1, t2, t3, t4, t5, t6, t7, t8, t9)
        return None
    }

    fun dequoteAll() {
        for (x in preArguments.indices) {
            if (preArguments[x] is String) {
                preArguments[x] = (preArguments[x] as String).dequote()
            }
        }
        for (x in input.indices) {
            input[x] = input[x].dequote()
        }
    }

    fun String.dequote(): String {
        return if (startsWith("\"") && endsWith("\"")) {
            substring(1, length - 1)
        } else {
            this
        }
    }


    public override fun clone(): CommandArgs<AUDIENCE> {
        return CommandArgs(listenerClass, listener, input, index).apply {
            this@apply.preArguments = this@CommandArgs.preArguments.toMutableList()
            this@apply.preArgumentIndex = this@CommandArgs.preArgumentIndex
        }
    }

    // Java-Compatible Methods
    @JvmName("nextArg")
    fun <T : Any> nextArg(cls: Class<T>, unit: DuoConsumer<CommandArgs<in AUDIENCE>, T>): Option<ConvertResult> {
        val sharedArg = this.clone()
        val t1 = sharedArg.consumeNextArg(cls.kotlin).getOrElse { return it.toOption() }
        unit.accept(sharedArg, t1)
        return None
    }

    @JvmName("nextArg")
    fun <T1 : Any, T2 : Any> nextArg(
        tc1: Class<T1>,
        tc2: Class<T2>,
        unit: TrioConsumer<CommandArgs<in AUDIENCE>, T1, T2>
    ): Option<ConvertResult> {
        val sharedArg = this.clone()
        val t1 = sharedArg.consumeNextArg(tc1.kotlin).getOrElse { return it.toOption() }
        val t2 = sharedArg.consumeNextArg(tc2.kotlin).getOrElse { return it.toOption() }
        unit.accept(sharedArg, t1, t2)
        return None
    }

    @JvmName("nextArg")
    fun <T1 : Any, T2 : Any, T3 : Any> nextArg(
        tc1: Class<T1>,
        tc2: Class<T2>,
        tc3: Class<T3>,
        unit: QuartetConsumer<CommandArgs<in AUDIENCE>, T1, T2, T3>
    ): Option<ConvertResult> {
        val sharedArg = this.clone()
        val t1 = sharedArg.consumeNextArg(tc1.kotlin).getOrElse { return it.toOption() }
        val t2 = sharedArg.consumeNextArg(tc2.kotlin).getOrElse { return it.toOption() }
        val t3 = sharedArg.consumeNextArg(tc3.kotlin).getOrElse { return it.toOption() }
        unit.accept(sharedArg, t1, t2, t3)
        return None
    }

    @JvmName("nextArg")
    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any> nextArg(
        tc1: Class<T1>,
        tc2: Class<T2>,
        tc3: Class<T3>,
        tc4: Class<T4>,
        unit: QuintetConsumer<CommandArgs<in AUDIENCE>, T1, T2, T3, T4>
    ): Option<ConvertResult> {
        val sharedArg = this.clone()
        val t1 = sharedArg.consumeNextArg(tc1.kotlin).getOrElse { return it.toOption() }
        val t2 = sharedArg.consumeNextArg(tc2.kotlin).getOrElse { return it.toOption() }
        val t3 = sharedArg.consumeNextArg(tc3.kotlin).getOrElse { return it.toOption() }
        val t4 = sharedArg.consumeNextArg(tc4.kotlin).getOrElse { return it.toOption() }
        unit.accept(sharedArg, t1, t2, t3, t4)
        return None
    }

    @JvmName("nextArg")
    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any> nextArg(
        tc1: Class<T1>,
        tc2: Class<T2>,
        tc3: Class<T3>,
        tc4: Class<T4>,
        tc5: Class<T5>,
        unit: SextetConsumer<CommandArgs<in AUDIENCE>, T1, T2, T3, T4, T5>
    ): Option<ConvertResult> {
        val sharedArg = this.clone()
        val t1 = sharedArg.consumeNextArg(tc1.kotlin).getOrElse { return it.toOption() }
        val t2 = sharedArg.consumeNextArg(tc2.kotlin).getOrElse { return it.toOption() }
        val t3 = sharedArg.consumeNextArg(tc3.kotlin).getOrElse { return it.toOption() }
        val t4 = sharedArg.consumeNextArg(tc4.kotlin).getOrElse { return it.toOption() }
        val t5 = sharedArg.consumeNextArg(tc5.kotlin).getOrElse { return it.toOption() }
        unit.accept(sharedArg, t1, t2, t3, t4, t5)
        return None
    }

    @JvmName("transformArgs")
    fun <T : Any> transformArgs(
        transformer: CommandArgTransformer<AUDIENCE, T>, consumer: Consumer<CommandArgs<T>>
    ) {
        val result = transformer.convert(this) ?: return
        consumer.accept(result)
    }

    @JvmName("transformArgsSilent")
    fun <T : Any> transformArgsSilent(
        transformer: CommandArgTransformer<AUDIENCE, T>, consumer: Consumer<CommandArgs<T>>
    ) {
        val result = transformer.convertSilent(this) ?: return
        consumer.accept(result)
    }

    data class ConvertResult(val success: Boolean, val exception: Throwable?) {
        fun onFailure(unit: (Throwable) -> Unit) {
            if (!success) {
                unit(exception!!)
            }
        }
    }
}