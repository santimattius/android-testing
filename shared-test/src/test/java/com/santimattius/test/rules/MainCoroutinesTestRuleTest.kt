package com.santimattius.test.rules

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.Description
import org.junit.runners.model.Statement

@OptIn(ExperimentalCoroutinesApi::class)
class MainCoroutinesTestRuleTest {

    @Test
    fun `default main dispatcher defers launch until scheduler runs`() {
        val rule = MainCoroutinesTestRule()

        withRule(rule) {
            val scope = CoroutineScope(Dispatchers.Main)
            try {
                var launched = false
                scope.launch { launched = true }

                assertFalse(launched)
                rule.testDispatcher.scheduler.runCurrent()
                assertTrue(launched)
            } finally {
                scope.cancel()
            }
        }
    }

    @Test
    fun `caller supplied dispatcher controls main scheduling`() {
        val dispatcher = UnconfinedTestDispatcher()
        val rule = MainCoroutinesTestRule(dispatcher)

        withRule(rule) {
            val scope = CoroutineScope(Dispatchers.Main)
            try {
                var launched = false
                scope.launch { launched = true }

                assertTrue(launched)
            } finally {
                scope.cancel()
            }
        }
    }

    private fun withRule(rule: MainCoroutinesTestRule, block: () -> Unit) {
        val statement = object : Statement() {
            override fun evaluate() = block()
        }
        rule.apply(statement, Description.createTestDescription(javaClass, "scheduler behavior"))
            .evaluate()
    }
}
