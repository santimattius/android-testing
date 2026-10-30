package com.santimattius.template.rules

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.platform.app.InstrumentationRegistry
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.test.FakeImageLoaderEngine
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Replaces Coil's loader with a fake engine that resolves every request instantly.
 *
 * Network image loading makes tests slow and non-hermetic.
 */
@OptIn(ExperimentalCoilApi::class)
class FakeImageLoaderRule : TestWatcher() {

    override fun starting(description: Description) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val engine = FakeImageLoaderEngine.Builder()
            .default(ColorDrawable(Color.DKGRAY))
            .build()
        Coil.setImageLoader(ImageLoader.Builder(context).components { add(engine) }.build())
    }

    override fun finished(description: Description) {
        Coil.reset()
    }
}
