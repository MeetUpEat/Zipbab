package com.bestapp.rice.data.network

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

object FirebaseClient {
    private val store = Firebase.firestore
    private val storage = Firebase.storage.reference

    val userStoreService = store.collection("user")
    val meetingStoreService = store.collection("meeting")

    val imageStorageService = storage.child("images/")
}