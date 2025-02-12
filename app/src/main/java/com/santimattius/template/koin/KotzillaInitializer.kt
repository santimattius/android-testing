package com.santimattius.template.koin

import android.content.Context
import androidx.startup.Initializer
import io.kotzilla.sdk.KotzillaSDK

class KotzillaInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        KotzillaSDK.setup(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}