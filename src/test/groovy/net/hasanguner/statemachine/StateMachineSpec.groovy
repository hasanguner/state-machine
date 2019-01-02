package net.hasanguner.statemachine


import kotlin.jvm.functions.Function1
import spock.lang.Specification

class StateMachineSpec extends Specification {

    static enum Event {
        X, Y, Z
    }

    static enum State {
        A, B, C, D
    }

    StateMachine<State, Event> stateMachine

    void setup() {
        stateMachine = StateMachine.withEntry(State.A, {

            it.shouldMoveFrom(State.A).to(State.B).on(Event.X, {
                println "I've moved from A to State[$it]"

            })

            it.shouldMoveTo(State.C).from(State.B).on(Event.Y) {
                println "I've moved from B to State[$it]"
            }

            it.shouldMoveTo(State.D).on(Event.Z, {})

            it.shouldExecuteOnTransition {
                println("I'm executed on every transition. $it")
            }


        })
    }

    def "should move from State A to State B on Event X"() {
        given:
        println "Current State : ${stateMachine.currentState}"
        when:
        stateMachine.evaluate(Event.X)
        then:
        println "State After Transition : ${stateMachine.currentState}"
        State.B == stateMachine.currentState
    }

    def "should move from State B to State C on Event Y"() {
        given:
        println "Current State : ${stateMachine.currentState}"
        stateMachine.evaluate(Event.X)
        when:
        stateMachine.evaluate(Event.Y)
        then:
        println "State After Transition : ${stateMachine.currentState}"
        State.C == stateMachine.currentState
    }

    def "should move from State A to State D on Event Z"() {
        given:
        println "Current State : ${stateMachine.currentState}"
        when:
        stateMachine.evaluate(Event.Z)
        then:
        println "State After Transition : ${stateMachine.currentState}"
        State.D == stateMachine.currentState
    }

    def "should not move from State A to anywhere on Event Y"() {
        given:
        println "Current State : ${stateMachine.currentState}"
        when:
        stateMachine.evaluate(Event.Y)
        then:
        def message = thrown InvalidTransitionException
        println "Exception : $message"
    }

    def "on transition callback must be executed on every transition"() {
        given:
        def callback = Mock(Function1)
        and:
        stateMachine = StateMachine.withEntry(State.A, {
            it.shouldMoveFrom(State.A).to(State.B).on(Event.X)
            it.shouldMove().from(State.B).to(State.C).on(Event.Y)
            it.onTransition(callback)
        })
        when:
        stateMachine.evaluate(Event.X)
        stateMachine.evaluate(Event.Y)
        then:
        2 * callback.invoke(_ as StateMachine.StateTransition)
    }

}
