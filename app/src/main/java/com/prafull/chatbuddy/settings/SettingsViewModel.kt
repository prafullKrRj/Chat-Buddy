package com.prafull.chatbuddy.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.chatbuddy.BuildConfig
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsViewModel : ViewModel(), KoinComponent {

    val mAuth by inject<FirebaseAuth>()
    private val firestore by inject<FirebaseFirestore>()
    private fun deleteCollection(collection: CollectionReference) {
        collection.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val batch = collection.firestore.batch()
                task.result?.forEach { document ->
                    batch.delete(document.reference)
                }
                batch.commit()
            }
        }
    }

    fun clearData() {
        viewModelScope.launch {
            val collection =
                firestore.collection("users").document(mAuth.currentUser?.email.toString())
                    .collection("history")
            deleteCollection(collection)
            showClearDataDialog = false
        }
    }

    fun signOut(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.WEB_CLIENT_ID)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut()
        mAuth.signOut()
    }

    var showClearDataDialog by mutableStateOf(false)
    var showLogoutDialog by mutableStateOf(false)
    var showDeleteAccountDialog by mutableStateOf(false)
}