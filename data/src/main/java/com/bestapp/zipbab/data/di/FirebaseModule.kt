package com.bestapp.zipbab.data.di


import com.bestapp.zipbab.data.FirestorDB.FirestoreDB
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object FirebaseModule {

    @Provides
    fun providesFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    fun providesFirebaseStorage(): FirebaseStorage = Firebase.storage

    @Singleton
    @Provides
    fun providesFirebaseDB(
        firebaseFirestore: FirebaseFirestore,
        firebaseStorage: FirebaseStorage,
    ): FirestoreDB = FirestoreDB(firebaseFirestore, firebaseStorage)

}
