package com.bestapp.rice.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import com.bestapp.rice.data.datastore.PlaceLocationPreferences
import com.bestapp.rice.data.datastore.PostPreferences
import com.bestapp.rice.data.datastore.UserPreferences
import com.bestapp.rice.data.model.remote.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class AppSettingRepositoryImpl(
    private val userPreferencesStore: DataStore<UserPreferences>,
): AppSettingRepository {

    override val userPreferencesFlow: Flow<UserPreferences> = userPreferencesStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(UserPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override suspend fun updateUserInfo(user: User) {
        userPreferencesStore.updateData { currentPreferences ->
            val builder = currentPreferences.toBuilder()
                .setId(user.id)
                .setNickName(user.nickName)
                .setId(user.id)
                .setPw(user.pw)
                .setProfileImage(user.profileImage)
                .setTemperature(user.temperature)
                .setMeetingCount(user.meetingCount)

            builder.clearPosts()
            user.posts.forEach { post ->
                val postBuilder = PostPreferences.newBuilder()
                    .setPostDocumentID(post.postDocumentID)
                post.images.forEach { image ->
                    postBuilder.addImages(image)
                }
                builder.addPosts(postBuilder.build())
            }
            builder.placeLocation = PlaceLocationPreferences.newBuilder()
                .setLocationAddress(user.placeLocation.locationAddress)
                .setLocationLat(user.placeLocation.locationLat)
                .setLocationLong(user.placeLocation.locationLong)
                .build()

            builder.build()
        }
    }

    override suspend fun getPrivacyInfo(): String {
        TODO("Not yet implemented")
    }

    override suspend fun removeUserInfo(): Boolean {
        TODO("Not yet implemented")
    }
}