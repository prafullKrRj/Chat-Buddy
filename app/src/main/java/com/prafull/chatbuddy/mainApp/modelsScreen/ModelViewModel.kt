package com.prafull.chatbuddy.mainApp.modelsScreen

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

    init {
        getModels()
        addModel(Model())
    }

    private var _modelResponse = MutableStateFlow(Response())
    val modelResponse = _modelResponse.asStateFlow()
    fun getModels() {
        _state.update { ModelScreenUIState(loading = true) }
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
                _state.update { ModelScreenUIState(loading = false) }
            } catch (e: Exception) {
                ModelScreenUIState(loading = false, error = true)
            }
        }
    }

    private fun addModel(model: Model) {
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("models").document("nlp").collection("Open AI")
                .document("gpt-3.5-turbo-0125")
                .set(
                        Model(
                                generalName = "GPT 3.5 Turbo",
                                actualName = "gpt-3.5-turbo-0125",
                                currPricePerToken = 0.0,
                                image = "https://firebasestorage.googleapis.com/v0/b/chat-buddy-4c59f.appspot.com/o/models%2Fchatgpt-icon.png?alt=media&token=bf729a3d-5e7a-40b7-b953-61e8966a1517",
                                hasVision = false,
                                hasFiles = false,
                                modelGroup = "Open AI",
                                taskType = "nlp",
                                temperature = 0.7,
                                system = Const.GENERAL_SYSTEM_PROMPT,
                                safetySetting = ModelSafety.UNSPECIFIED.name
                        )
                )
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

enum class ModelSafety {
    ROMANTIC,
    FRIENDLY,
    PROFESSIONAL,
    ACTOR,
    UNSPECIFIED
}