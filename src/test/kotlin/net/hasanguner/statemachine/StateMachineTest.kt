package net.hasanguner.statemachine

import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class StateMachineTest {

    enum class Event {
        X, Y, Z
    }

    enum class State {
        A, B, C, D
    }

    lateinit var stateMachine: StateMachine<State, Event>

    @Before
    fun setup() {
        stateMachine = StateMachine.withEntry(State.A) {

            (shouldMoveFrom(State.A) to State.B).on(Event.X) {
                println("I've moved from A to State[$it]")
            }

            (shouldMoveTo(State.C) from State.B).on(Event.Y) {
                println("I've moved from B to State[$it]")
            }

            shouldMoveTo(State.D).on(Event.Z)

            onTransition {
                println("I'm executed on every transition. $it")
            }
        }
    }

    @Test
    fun shouldMoveFrom_StateA_to_StateB_on_EventX() {
        //given
        println("Current State : ${stateMachine.currentState}")
        //when
        stateMachine.evaluate(Event.X)
        //then
        println("State After Transition : ${stateMachine.currentState}")
        assertEquals(State.B, stateMachine.currentState)
    }

    @Test
    fun shouldMoveFrom_StateB_to_StateC_on_EventY() {
        //given
        println("Current State : ${stateMachine.currentState}")
        stateMachine.currentState = State.B
        //when
        stateMachine.evaluate(Event.Y)
        //then
        println("State After Transition : ${stateMachine.currentState}")
        assertEquals(State.C, stateMachine.currentState)
    }

    @Test
    fun shouldMoveFrom_StateA_to_StateD_on_EventZ() {
        //given
        println("Current State : ${stateMachine.currentState}")
        //when
        stateMachine.evaluate(Event.Z)
        //then
        println("State After Transition : ${stateMachine.currentState}")
        assertEquals(State.D, stateMachine.currentState)
    }

    @Test(expected = InvalidTransitionException::class)
    fun shouldNotMoveFrom_StateA_to_anywhere_on_EventY() {
        //given
        println("Current State : ${stateMachine.currentState}")
        //when
        stateMachine.evaluate(Event.Y)
        //then
    }

}
