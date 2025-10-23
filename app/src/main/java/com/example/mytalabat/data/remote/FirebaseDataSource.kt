package com.example.mytalabat.data.remote

import android.util.Log
import com.example.mytalabat.data.model.User
import com.example.mytalabat.data.model.UserProfile
import com.example.mytalabat.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseDataSource {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance("https://mytalabat2-default-rtdb.europe-west1.firebasedatabase.app")
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun signIn(email: String, password: String): User {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user ?: throw Exception("Authentication failed")
        return User(firebaseUser.uid, firebaseUser.email ?: "")
    }

    suspend fun signUp(email: String, password: String): User {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user ?: throw Exception("Registration failed")
        return User(firebaseUser.uid, firebaseUser.email ?: "")
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        Log.d("FirebaseDataSource", "Saving profile: $profile")
        database.getReference(Constants.PROFILES_REF)
            .child(profile.uid)
            .setValue(profile.toMap())
            .await()
        Log.d("FirebaseDataSource", "Profile saved successfully")
    }

    /**
     * Fetches full user profile, including the 'isSeller' flag,
     * with safe parsing of all fields.
     */
    suspend fun getUserProfile(uid: String): UserProfile? = suspendCancellableCoroutine { continuation ->
        val ref = database.getReference(Constants.PROFILES_REF).child(uid)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FirebaseDataSource", "Snapshot exists: ${snapshot.exists()}")
                Log.d("FirebaseDataSource", "Snapshot value: ${snapshot.value}")

                if (!snapshot.exists()) {
                    if (continuation.isActive) continuation.resume(null)
                    return
                }

                try {
                    // Safely read each field (handles missing/null values)
                    val uidValue = snapshot.child("uid").getValue(String::class.java) ?: uid
                    val name = snapshot.child("name").getValue(String::class.java) ?: ""
                    val email = snapshot.child("email").getValue(String::class.java) ?: ""
                    val phone = snapshot.child("phoneNumber").getValue(String::class.java) ?: ""
                    val isSeller = snapshot.child("isSeller").getValue(Boolean::class.java) ?: false
                    val createdAt = snapshot.child("createdAt").getValue(Long::class.java) ?: 0L
                    // ✅ ADD THIS LINE - Read profile photo URL
                    val profilePhotoUrl = snapshot.child("profilePhotoUrl").getValue(String::class.java) ?: ""

                    val profile = UserProfile(
                        uid = uidValue,
                        name = name,
                        email = email,
                        phoneNumber = phone,
                        isSeller = isSeller,
                        profilePhotoUrl = profilePhotoUrl,  // ✅ Include it here

                        createdAt = createdAt
                    )

                    Log.d("FirebaseDataSource", "Parsed profile: $profile")
                    if (continuation.isActive) continuation.resume(profile)
                } catch (e: Exception) {
                    Log.e("FirebaseDataSource", "Error parsing profile: ${e.message}")
                    if (continuation.isActive) continuation.resumeWithException(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseDataSource", "Database error: ${error.message}")
                if (continuation.isActive) continuation.resumeWithException(error.toException())
            }
        }

        ref.addListenerForSingleValueEvent(listener)
        continuation.invokeOnCancellation { ref.removeEventListener(listener) }
    }

    suspend fun updateUserProfile(uid: String, updates: Map<String, Any>) {
        database.getReference(Constants.PROFILES_REF)
            .child(uid)
            .updateChildren(updates)
            .await()
    }

    fun signOut() {
        auth.signOut()
    }
}