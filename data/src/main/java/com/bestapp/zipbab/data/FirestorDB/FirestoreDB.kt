package com.bestapp.zipbab.data.FirestorDB

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import javax.inject.Inject

class FirestoreDB @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
){
    fun getPrivacyDB(): CollectionReference{
        return firebaseFirestore.collection("privacy")
    }

    fun getCategoryDB(): CollectionReference{
        return firebaseFirestore.collection("category")
    }

    fun getUsersDB(): CollectionReference{
        return firebaseFirestore.collection("users")
    }

    fun getMeetingDB(): CollectionReference{
        return firebaseFirestore.collection("meeting")
    }

    fun getImagesDB(): StorageReference{
        return firebaseStorage.reference.child("images/")
    }

    fun getPostDB(): CollectionReference{
        return firebaseFirestore.collection("posts")
    }

    fun getReportDB(): CollectionReference{
        return firebaseFirestore.collection("report")
    }

}