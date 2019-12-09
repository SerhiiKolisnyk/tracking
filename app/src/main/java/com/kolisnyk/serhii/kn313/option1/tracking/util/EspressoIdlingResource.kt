package com.kolisnyk.serhii.kn313.option1.tracking.util

import android.support.test.espresso.IdlingResource

object EspressoIdlingResource {

    private val RESOURCE = "GLOBAL"

    private val mCountingIdlingResource = SimpleCountingIdlingResource(RESOURCE)

    fun increment() {
        mCountingIdlingResource.increment()
    }

    fun decrement() {
        mCountingIdlingResource.decrement()
    }

    val idlingResource: IdlingResource
        get() = mCountingIdlingResource
}
