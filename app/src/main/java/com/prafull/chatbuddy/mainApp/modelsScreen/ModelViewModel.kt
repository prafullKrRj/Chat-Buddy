package com.prafull.chatbuddy.mainApp.modelsScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.chatbuddy.model.Model
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

    init {
        getModels()
    }

    private var _modelResponse = MutableStateFlow(Response())
    val modelResponse = _modelResponse.asStateFlow()
    fun getModels() {
        _state.update { ModelScreenUIState(loading = true) }
        Log.d("ModelViewModel", "Getting Models")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val modelDocuments = firestore.collection("models").get().await()
                val modelResponses = modelDocuments.documents.map { document ->
                    val type = document.id
                    val groups = document["groups"] as? List<String> ?: emptyList()

                    val groupList = groups.map { groupName ->
                        val modelsCollection =
                            firestore.collection("models").document(type).collection(groupName)
                                .get().await()
                        val models = modelsCollection.documents.map { modelDocument ->
                            modelDocument.toObject(Model::class.java) ?: Model()
                        }
                        Group(name = groupName, models = models)
                    }
                    ModelResponse(type = type, groups = groupList)
                }

                _modelResponse.update { Response(modelResponses) }
                Log.d("ModelViewModel", "Model Response: $modelResponses")
                _state.update { ModelScreenUIState(loading = false) }
            } catch (e: Exception) {
                ModelScreenUIState(loading = false, error = true)
            }
        }
    }

    private fun addModel(model: Model) {
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("models").document("nlp").collection("Characters")
                .document("Lucia").set(
                        Model(
                                generalName = "Lucia",
                                actualName = "gemini-1.5-flash",
                                currPricePerToken = 0.0,
                                image = "",
                                hasVision = false,
                                hasFiles = false,
                                modelGroup = "Characters",
                                taskType = "AI Girlfriend"
                        )
                ).await()
        }
    }
}

data class Response(
    val modelResponses: List<ModelResponse> = emptyList()
)

data class ModelResponse(
    val type: String = "",
    val groups: List<Group> = emptyList(),
)

data class Group(
    val name: String = "",
    val models: List<Model> = emptyList()
)

data class ModelScreenUIState(
    val models: List<Model> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false
)