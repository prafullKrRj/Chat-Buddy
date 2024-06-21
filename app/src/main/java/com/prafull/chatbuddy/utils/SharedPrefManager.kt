package com.prafull.chatbuddy.utils

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.prafull.chatbuddy.model.Model
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SharedPrefManager(context: Context) : KoinComponent {
    private val pref = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
    private val gson by inject<Gson>()
    private val firestore by inject<FirebaseFirestore>()
    fun setModel(model: Model) {
        Log.d("SharedPrefManager", "setModel: $model")
        pref.edit().putString("currModel", gson.toJson(model)).apply()
    }

    suspend fun getModel(): Model {
        if (pref.contains("currModel")) {
            val x = pref.getString("currModel", "")
            return gson.fromJson(x, Model::class.java)
        } else {
            val model = firestore.collection("model").document("nlp").collection(Const.CHAT_BUDDY)
                .document(Const.CHAT_BUDDY).get().await().toObject(Model::class.java)
            model?.let {
                setModel(it)
                return it
            }
            return Model()
        }
    }
}
