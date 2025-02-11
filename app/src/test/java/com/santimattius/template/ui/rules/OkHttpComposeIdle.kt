package com.santimattius.template.ui.rules

import androidx.compose.ui.test.IdlingResource
import com.jakewharton.espresso.OkHttp3IdlingResource

class OkHttpComposeIdle(private val idle: OkHttp3IdlingResource) : IdlingResource {
    override val isIdleNow: Boolean
        get() = idle.isIdleNow
}