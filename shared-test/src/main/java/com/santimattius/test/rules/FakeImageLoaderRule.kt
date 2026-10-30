package com.santimattius.test.rules

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.core.app.ApplicationProvider
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.test.FakeImageLoaderEngine
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Replaces Coil's loader with a fake engine that resolves every request immediately.
 *
 * Real image requests run on background dispatchers. In tests they make the suite depend on the
 * network or disk, and a request that finishes late can touch a lifecycle off the main thread and
 * fail an unrelated test. Works on a device and under Robolectric.
 */
@OptIn(ExperimentalCoilApi::class)
class FakeImageLoaderRule : TestWatcher() {

    override fun starting(description: Description) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val engine = FakeImageLoaderEngine.Builder()
            .default(ColorDrawable(Color.DKGRAY))
            .build()
        Coil.setImageLoader(ImageLoader.Builder(context).components { add(engine) }.build())
    }

    override fun finished(description: Description) {
        Coil.reset()
    }
}
