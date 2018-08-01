package net.hasanguner.statemachine;

import org.junit.Before;
import org.junit.Test;

import static net.hasanguner.statemachine.FunctionUtils.action;
import static junit.framework.TestCase.assertEquals;

public class StateMachineJavaTest {

    enum Event {
        X, Y, Z
    }

    enum State {
        A, B, C, D
    }

    private StateMachine<State, Event> stateMachine;

    @Before
    public void setup() {
        stateMachine = StateMachine.withEntry(State.A, it -> {

            it.shouldMoveFrom(State.A).to(State.B).on(Event.X, action(
                    state -> System.out.println("I've moved from A to State[" + state + "]"))
            );

            it.shouldMoveTo(State.C).from(State.B).on(Event.Y, action(
                    state -> System.out.println("I've moved from B to State[" + state + "]"))
            );

            it.shouldMove().to(State.D).on(Event.Z);

            return it;
        });
    }

    @Test
    public void shouldMoveFrom_StateA_to_StateB_on_EventX() {
        //given
        System.out.println("Current State : " + stateMachine.getCurrentState());
        //when
        stateMachine.evaluate(Event.X);
        //then
        System.out.println("State After Transition : " + stateMachine.getCurrentState());
        assertEquals(State.B, stateMachine.getCurrentState());
    }

    @Test
    public void shouldMoveFrom_StateB_to_StateC_on_EventY() {
        //given
        System.out.println("Current State : " + stateMachine.getCurrentState());
        stateMachine.setCurrentState(State.B);
        //when
        stateMachine.evaluate(Event.Y);
        //then
        System.out.println("State After Transition : " + stateMachine.getCurrentState());
        assertEquals(State.C, stateMachine.getCurrentState());
    }

    @Test
    public void shouldMoveFrom_StateA_to_StateD_on_EventZ() {
        //given
        System.out.println("Current State : " + stateMachine.getCurrentState());
        //when
        stateMachine.evaluate(Event.Z);
        //then
        System.out.println("State After Transition : " + stateMachine.getCurrentState());
        assertEquals(State.D, stateMachine.getCurrentState());
    }

    @Test(expected = InvalidTransitionException.class)
    public void shouldNotMoveFrom_StateA_to_anywhere_on_EventY() {
        //given
        System.out.println("Current State : " + stateMachine.getCurrentState());
        //when
        stateMachine.evaluate(Event.Y);
        //then
    }

}
