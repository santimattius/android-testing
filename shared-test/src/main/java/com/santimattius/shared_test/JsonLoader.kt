package com.santimattius.shared_test

import java.io.InputStreamReader


class JsonLoader {

    fun load(file: String): String {
        val loader = InputStreamReader(this.javaClass.classLoader?.getResourceAsStream(file))
        val jsonStr = loader.readText()
        loader.close()
        return jsonStr
    }
}