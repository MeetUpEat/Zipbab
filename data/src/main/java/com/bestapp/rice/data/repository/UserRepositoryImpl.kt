package com.bestapp.rice.data.repository

import android.graphics.Bitmap
import com.bestapp.rice.data.model.remote.Image
import com.bestapp.rice.data.model.remote.PlaceLocation
import com.bestapp.rice.data.model.remote.Post
import com.bestapp.rice.data.model.remote.Review
import com.bestapp.rice.data.model.remote.User

class UserRepositoryImpl : UserRepository {
    override suspend fun getUser(userDocumentId: String): User {
        return User(
            userDocumentID = "1q2W3e4R1q2W3e4R1q2W3e4R",
            nickName = "집밥이",
            id = "aaaa@naver.com",
            pw = "q1w2e3r4",
            profileImage = "https://media.discordapp.net/attachments/1237022986410922047/1245701361891283106/sample_profile_image.png?ex=6659b54c&is=665863cc&hm=fb148c46237321ff56406da31c4e5d618d2cabd04e9e5bc12c89369184856c93&=&format=webp&quality=lossless&width=84&height=84",
            temperature = 41.4,
            meetingCount = 100,
            posts = listOf(
                Post(
                    postDocumentID = "1", images = listOf(
                        Image(
                            "1",
                            "https://images.unsplash.com/photo-1577110058859-74547db40bc0?q=80&w=3024&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                        ),
                        Image(
                            "2",
                            "https://images.unsplash.com/photo-1577110058859-74547db40bc0?q=80&w=3024&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                        ),
                        Image(
                            "3",
                            "https://images.unsplash.com/photo-1577110058859-74547db40bc0?q=80&w=3024&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                        ),
                    )
                ),
                Post(
                    postDocumentID = "2", images = listOf(
                        Image(
                            "1",
                            "https://images.unsplash.com/photo-1577110058859-74547db40bc0?q=80&w=3024&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                        ),
                    )
                ),
                Post(
                    postDocumentID = "3", images = listOf(
                        Image(
                            "1",
                            "https://images.unsplash.com/photo-1577110058859-74547db40bc0?q=80&w=3024&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                        ),
                    )
                ),
                Post(
                    postDocumentID = "4", images = listOf(
                        Image(
                            "1",
                            "https://images.unsplash.com/photo-1577110058859-74547db40bc0?q=80&w=3024&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                        ),
                    )
                ),
                Post(
                    postDocumentID = "5", images = listOf(
                        Image(
                            "1",
                            "https://images.unsplash.com/photo-1577110058859-74547db40bc0?q=80&w=3024&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                        ),
                    )
                ),

                ),
            placeLocation = PlaceLocation(
                locationAddress = "",
                locationLat = "",
                locationLong = ""
            )
        )
    }

    override suspend fun login(): User {
        TODO("Not yet implemented")
    }

    override suspend fun signUpUs(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserTemperature(reviews: List<Review>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserMeetingCount() {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserProfileImage(userID: String, profileImageUri: String?) {
        TODO("Not yet implemented")
    }

    override suspend fun convertImages(userDocumentID: String, images: List<Bitmap>): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun addPost(userID: String, post: Post): Boolean {
        TODO("Not yet implemented")
    }
}