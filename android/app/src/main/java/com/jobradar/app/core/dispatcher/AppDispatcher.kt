package com.jobradar.app.core.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * A single injectable holder of coroutine dispatchers.
 *
 * Clean Architecture discipline: the domain/data layers must never reference
 * `Dispatchers.Main/IO` directly (which would tie them to the Android runtime
 * and to concrete threading). Instead they receive an [AppDispatcher]. This is
 * also what makes the whole thing trivially testable — inject a test dispatcher.
 */
interface AppDispatcher {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
    val mainImmediate: CoroutineDispatcher
}

class DefaultAppDispatcher : AppDispatcher {
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val mainImmediate: CoroutineDispatcher = Dispatchers.Main.immediate
}
