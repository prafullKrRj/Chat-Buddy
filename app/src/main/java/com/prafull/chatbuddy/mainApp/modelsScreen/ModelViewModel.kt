package com.prafull.chatbuddy.mainApp.modelsScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.chatbuddy.model.Model
import com.prafull.chatbuddy.utils.Const
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ModelViewModel : ViewModel(), KoinComponent {

    private val firestore by inject<FirebaseFirestore>()
    private val _state = MutableStateFlow(ModelScreenUIState())
    val state = _state.asStateFlow()

    var claudeModels by mutableStateOf(emptyList<Model>())
    var gptModels by mutableStateOf(emptyList<Model>())
    var geminiModels by mutableStateOf(emptyList<Model>())

    init {
        getModels()
    }

    fun getModels() {
        _state.update { ModelScreenUIState(loading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = firestore.collection("models").get().await().documents.mapNotNull {
                    it.toObject(Model::class.java)
                }
                _state.update {
                    ModelScreenUIState(models = response, loading = false, error = false)
                }
                claudeModels = state.value.models.filter {
                    it.modelGroup == Const.CLAUDE
                }
                geminiModels = state.value.models.filter {
                    it.modelGroup == Const.GEMINI
                }
                gptModels = state.value.models.filter {
                    it.modelGroup == Const.GPT
                }
            } catch (e: Exception) {
                ModelScreenUIState(loading = false, error = true)
            }
        }
    }
}

data class ModelScreenUIState(
    val models: List<Model> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false
)