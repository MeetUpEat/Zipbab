package com.bestapp.zipbab.data

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.tasks.await

suspend fun <T> Task<T>.doneSuccessful(): Boolean {
    await()
    return isSuccessful
}

