package com.threes.scenespotinseoul.utilities

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

const val THREAD_COUNT = 3

class AppExecutors(
    private val diskIO: Executor = DiskIOThreadExecutor(),
    private val networkIO: Executor = Executors.newFixedThreadPool(THREAD_COUNT),
    private val mainThread: Executor = MainThreadExecutor()
) {
    fun diskIO(): Executor = diskIO

    fun networkIO(): Executor = networkIO

    fun mainThread(): Executor = mainThread

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

    private class DiskIOThreadExecutor : Executor {

        private val diskIO = Executors.newSingleThreadExecutor()

        override fun execute(command: Runnable) {
            diskIO.execute(command)
        }
    }
}