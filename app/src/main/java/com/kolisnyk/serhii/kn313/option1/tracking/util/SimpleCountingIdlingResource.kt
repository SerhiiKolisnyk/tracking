package com.kolisnyk.serhii.kn313.option1.tracking.util

import android.support.test.espresso.IdlingResource

import java.util.concurrent.atomic.AtomicInteger

import com.google.common.base.Preconditions.checkNotNull


class SimpleCountingIdlingResource(resourceName: String) : IdlingResource {

    private val mResourceName: String

    private val counter = AtomicInteger(0)

    // written from main thread, read from any thread.
    @Volatile private var resourceCallback: IdlingResource.ResourceCallback? = null

    init {
        mResourceName = checkNotNull(resourceName)
    }

    override fun getName(): String {
        return mResourceName
    }

    override fun isIdleNow(): Boolean {
        return counter.get() == 0
    }

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        this.resourceCallback = resourceCallback
    }

    /**
     * Increments the count of in-flight transactions to the resource being monitored.
     */
    fun increment() {
        counter.getAndIncrement()
    }

    /**
     * Decrements the count of in-flight transactions to the resource being monitored.

     * If this operation results in the counter falling below 0 - an exception is raised.

     * @throws IllegalStateException if the counter is below 0.
     */
    fun decrement() {
        val counterVal = counter.decrementAndGet()
        if (counterVal == 0) {
            // we've gone from non-zero to zero. That means we're idle now! Tell espresso.
            if (null != resourceCallback) {
                resourceCallback!!.onTransitionToIdle()
            }
        }

        if (counterVal < 0) {
            throw IllegalArgumentException("Counter has been corrupted!")
        }
    }
}
