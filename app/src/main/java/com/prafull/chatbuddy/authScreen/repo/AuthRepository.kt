package com.prafull.chatbuddy.authScreen.repo

import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.chatbuddy.authScreen.models.User
import kotlinx.coroutines.tasks.await


class AuthRepository {
    private val firestore = FirebaseFirestore.getInstance()

    private suspend fun checkUserExists(email: String): User? {
        val document = firestore.collection("users").document(email).get().await()
        return document.toObject(User::class.java)
    }

    suspend fun createUser(name: String, email: String): Boolean {
        val user = checkUserExists(email)
        if (user == null) {
            val startCoins = getStartCoins()
            val newUser = User(name, email, currCoins = startCoins)
            var result = false
            firestore.collection("users").document(email).set(newUser).addOnSuccessListener {
                result = true
            }.addOnFailureListener {
                result = false
            }
            return result
        } else {
            return false
        }
    }

    private suspend fun getStartCoins(): Long {
        val document = firestore.collection("startCoins").document("startCoins").get().await()
        val coins = document.getLong("startCoins")
        return coins ?: 0
    }
}