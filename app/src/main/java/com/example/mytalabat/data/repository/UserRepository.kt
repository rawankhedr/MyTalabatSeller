package com.example.mytalabat.data.repository

import com.example.mytalabat.data.model.UserProfile
import com.example.mytalabat.data.remote.FirebaseDataSource
import com.example.mytalabat.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val dataSource: FirebaseDataSource) {

    suspend fun saveUserProfile(profile: UserProfile): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                dataSource.saveUserProfile(profile)
                Resource.Success(Unit)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to save profile")
            }
        }
    }

    /**
     * Returns a Resource<UserProfile> by wrapping the result
     * from FirebaseDataSource (which returns UserProfile?).
     */
    suspend fun getUserProfile(uid: String): Resource<UserProfile> {
        return withContext(Dispatchers.IO) {
            try {
                val profile = dataSource.getUserProfile(uid)
                if (profile != null) {
                    Resource.Success(profile)
                } else {
                    Resource.Error("User profile not found")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to fetch user profile")
            }
        }
    }

    suspend fun updateUserProfile(uid: String, updates: Map<String, Any>): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                dataSource.updateUserProfile(uid, updates)
                Resource.Success(Unit)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to update profile")
            }
        }
    }

    /**
     * Updates only the profile picture URL in Firebase.
     */
    suspend fun updateProfilePictureUrl(uid: String, url: String): Resource<Unit> {
        return updateUserProfile(uid, mapOf("profilePhotoUrl" to url))
    }
}
