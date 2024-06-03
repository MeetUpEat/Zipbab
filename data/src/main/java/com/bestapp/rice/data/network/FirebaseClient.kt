package com.bestapp.rice.data.network

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

object FirebaseClient {
    val store = Firebase.firestore
    private val storage = Firebase.storage.reference

    val userStoreService = store.collection("users")
    val meetingStoreService = store.collection("meeting")
    val categoryStoreService = store.collection("category")

    val imageStorageService = storage.child("images/")
}