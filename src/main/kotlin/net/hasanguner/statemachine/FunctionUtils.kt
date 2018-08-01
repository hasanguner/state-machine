package net.hasanguner.statemachine

import java.util.function.Consumer

object FunctionUtils {

    @JvmStatic
    fun <T> fromConsumer(consumer: Consumer<T>): Function1<T, Unit> =
            { consumer.accept(it) }

    @JvmStatic
    fun <T> action(consumer: Consumer<T>): Function1<T, Unit> =
            fromConsumer(consumer)


}