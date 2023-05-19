package com.santimattius.template.espresso


import androidx.appcompat.widget.Toolbar
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`

object ToolbarMatcher {

    fun onToolbarWithTitle(title: CharSequence): ViewInteraction =
        onView(isAssignableFrom(Toolbar::class.java)).check(
            matches(
                withToolbarTitle(
                    `is`<CharSequence>(
                        title
                    )
                )
            )
        )

    private fun withToolbarTitle(textMatcher: Matcher<CharSequence>): Matcher<Any> {
        return object : BoundedMatcher<Any, Toolbar>(Toolbar::class.java) {
            public override fun matchesSafely(toolbar: Toolbar): Boolean =
                textMatcher.matches(toolbar.title)

            override fun describeTo(description: Description) {
                description.appendText("with toolbar title: ")
                textMatcher.describeTo(description)
            }
        }
    }
}