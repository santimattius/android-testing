package com.santimattius.test.compose

/**
 * Thin Given/When/Then wrapper over a screen robot.
 *
 * Steps run immediately, in the order they are written, so any assertion failure fails the test.
 * The functions only name the intent of each block; they add no scheduling or reporting.
 */
fun <R> scenario(robot: R, steps: Scenario<R>.() -> Unit) = Scenario(robot).steps()

class Scenario<R>(private val robot: R) {
    /** Preconditions, typically state injected straight into the screen under test. */
    fun given(step: R.() -> Unit) = robot.step()

    /** The user or system action. Named `whenever` because `when` is a Kotlin keyword. */
    fun whenever(step: R.() -> Unit) = robot.step()

    /** The observable outcome. */
    fun then(step: R.() -> Unit) = robot.step()
}
