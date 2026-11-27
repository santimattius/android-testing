package com.santimattius.test.compose

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ScenarioTest {

    private class RecordingRobot {
        val steps = mutableListOf<String>()
    }

    @Test
    fun runsEveryStepInOrderAgainstTheRobot() {
        val robot = RecordingRobot()

        scenario(robot) {
            given { steps += "given" }
            whenever { steps += "when" }
            then { steps += "then" }
        }

        assertEquals(listOf("given", "when", "then"), robot.steps)
    }

    @Test
    fun aFailingThenStepFailsTheScenario() {
        assertThrows(AssertionError::class.java) {
            scenario(RecordingRobot()) {
                then { throw AssertionError("expected failure") }
            }
        }
    }
}
