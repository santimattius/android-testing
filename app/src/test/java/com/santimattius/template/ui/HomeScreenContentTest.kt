package com.santimattius.template.ui

import android.app.Application
import android.os.Build
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R], application = Application::class)
class HomeScreenContentTest : HomeScreenContentScenarios()
