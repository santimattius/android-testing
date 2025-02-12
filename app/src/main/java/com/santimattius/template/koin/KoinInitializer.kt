package com.santimattius.template.koin

import androidx.startup.Initializer
import org.koin.core.Koin
import org.koin.androix.startup.KoinInitializer as KoinOriginalInitializer

class KoinInitializer : Initializer<Koin> by KoinOriginalInitializer() {
    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf(KotzillaInitializer::class.java)
    }
}