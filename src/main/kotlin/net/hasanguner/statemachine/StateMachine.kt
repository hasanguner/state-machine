package net.hasanguner.statemachine

class StateMachine<S : Any, E : Any> private constructor(
        entryState: S,
        private val transitions: Set<StateTransition<S, E>>,
        private val onTransitionCallback: ((StateTransition<S, E>) -> Unit)
) {

    var currentState: S = entryState

    fun evaluate(event: E) = validate(event)
            .also { currentState = it.to }
            .also(onTransitionCallback)
            .action(currentState)

    private fun validate(event: E): StateTransition<S, E> =
            transitions.firstOrNull { event == it.on && (null == it.from || currentState == it.from) }
                    ?: throw InvalidTransitionException(event.toString(), currentState.toString())

    companion object {
        @JvmStatic
        fun <S : Any, E : Any> withEntry(state: S, builder: StateMachineBuilder<S, E>.() -> StateMachineBuilder<S, E>): StateMachine<S, E> =
                StateMachineBuilder<S, E>(state).builder().build()
    }

    class StateMachineBuilder<S : Any, E : Any> internal constructor(private val entryState: S) {

        private val transitions = mutableSetOf<StateTransition<S, E>>()
        private var onTransitionCallback: (StateTransition<S, E>) -> Unit = {}

        fun shouldMove(): StateTransitionBuilder<S, E> = StateTransitionBuilder {
            transitions += it.build()
            this@StateMachineBuilder
        }

        fun shouldMoveFrom(state: S): StateTransitionBuilder<S, E> = StateTransitionBuilder<S, E> {
            transitions += it.build()
            this@StateMachineBuilder
        }.apply { from(state) }

        fun shouldMoveTo(state: S): StateTransitionBuilder<S, E> = StateTransitionBuilder<S, E> {
            transitions += it.build()
            this@StateMachineBuilder
        }.apply { to(state) }

        fun shouldExecuteOnTransition(action: (StateTransition<S, E>) -> Unit): StateMachineBuilder<S, E> = onTransition(action)

        fun onTransition(action: (StateTransition<S, E>) -> Unit): StateMachineBuilder<S, E> = apply {
            onTransitionCallback = action
        }

        internal fun build(): StateMachine<S, E> = StateMachine(entryState, transitions, onTransitionCallback)

    }

    data class StateTransition<S, E>(val from: S? = null, val to: S, val on: E, val action: (S) -> Unit)

    class StateTransitionBuilder<S : Any, E : Any> internal constructor(private val finalizer: (StateTransitionBuilder<S, E>) -> StateMachineBuilder<S, E>) {

        private var from: S? = null
        private lateinit var to: S
        private lateinit var on: E
        private lateinit var action: (S) -> Unit

        infix fun from(state: S): StateTransitionBuilder<S, E> = apply { from = state }

        infix fun to(state: S): StateTransitionBuilder<S, E> = apply { to = state }

        @JvmOverloads
        fun on(event: E, action: (S) -> Unit = {}): StateMachineBuilder<S, E> =
                apply {
                    this@StateTransitionBuilder.on = event
                    this@StateTransitionBuilder.action = action
                }.let(finalizer)

        internal fun build(): StateTransition<S, E> = StateTransition(from, to, on, action)

    }

}

class InvalidTransitionException(event: String, state: String) : RuntimeException("No transition found for event[$event] from[$state].")//todo message
