package com.threes.scenespotinseoul.utilities

fun runOnMain(execute: () -> Unit) = runOnMain(Runnable { execute() })

fun runOnMain(execute: Runnable) {
    AppExecutors().mainThread().execute(execute)
}

fun runOnDiskIO(execute: () -> Unit) = runOnDiskIO(Runnable { execute() })

fun runOnDiskIO(execute: Runnable) {
    AppExecutors().diskIO().execute(execute)
}

fun runOnNetworkIO(execute: () -> Unit) = runOnNetworkIO(Runnable { execute() })

fun runOnNetworkIO(execute: Runnable) {
    AppExecutors().networkIO().execute(execute)
}