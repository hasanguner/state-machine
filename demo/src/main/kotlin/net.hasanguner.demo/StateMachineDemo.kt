package net.hasanguner.demo

import net.hasanguner.statemachine.StateMachine

enum class State {
    FOOD_PLACED,
    DOOR_CLOSED,
    COOKING,
    COOKING_INTERRUPTED,
    COOKING_FINISHED
}

enum class Event {
    CLOSE_DOOR,
    SET_TIMER,
    OPEN_DOOR,
    TIME_IS_UP
}

val stateMachine = StateMachine.withEntry<State, Event>(State.FOOD_PLACED) {

    (shouldMove() from State.FOOD_PLACED to State.DOOR_CLOSED).on(Event.CLOSE_DOOR) {
        println("Door closed.")
    }

    (shouldMove() from State.DOOR_CLOSED to State.COOKING).on(Event.SET_TIMER) {
        println("Cooking started.")
    }

    (shouldMove() from State.COOKING to State.COOKING_INTERRUPTED).on(Event.OPEN_DOOR) {
        println("Cooking interrupted.")
    }

    (shouldMove() from State.COOKING_INTERRUPTED to State.COOKING).on(Event.CLOSE_DOOR) {
        println("Cooking continues.")
    }

    (shouldMove() from State.COOKING to State.COOKING_FINISHED).on(Event.TIME_IS_UP) {
        println("Cooking finised.")
    }

}

fun main(args: Array<String>) {

    println("-Initial State : [${stateMachine.currentState}]")

    println("*Closing door..")
    stateMachine.evaluate(Event.CLOSE_DOOR)
    println("*Setting timer..")
    stateMachine.evaluate(Event.SET_TIMER)

    println("-A little naughtiness :)")
    repeat(2) {
        //naughty developer actions..
        println("*Opening door..")
        stateMachine.evaluate(Event.OPEN_DOOR)
        println("*Closing door..")
        stateMachine.evaluate(Event.CLOSE_DOOR)
    }
    println("-Okay, let's let it cook")
    println("*Time is up..")
    stateMachine.evaluate(Event.TIME_IS_UP)

    println("-Final State : [${stateMachine.currentState}]")
}
