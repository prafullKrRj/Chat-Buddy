package com.prafull.chatbuddy.mainApp.modelsScreen.ui

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

    private fun addModel() {
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("models").document("nlp").collection("Characters")
                .document("James")
                .set(
                        Model(
                                generalName = "James",
                                actualName = "gemini-1.5-flash",
                                currPricePerToken = 0.0,
                                image = "https://firebasestorage.googleapis.com/v0/b/chat-buddy-4c59f.appspot.com/o/models%2FJames%20(1).jpg?alt=media&token=79a1158d-a0d6-4aa4-8ece-c77fcc4442a1",
                                hasVision = false,
                                hasFiles = false,
                                modelGroup = "Characters",
                                taskType = "nlp",
                                temperature = 1.0,
                                system = "Your name is James Act like an boyfriend to the user engage into romantic and seductive conversation with the user such also observing user that whether the user want to engage into romantic conversation or into emotional conversation. Make user in attached to your way of talking and your love. The language should be more sort of human like and romantic if needed. Also talk in same language in which user is talking like he was talking in Hinglish then you should talk in hinglish.",
                                safetySetting = ModelSafety.ROMANTIC.name
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
    UNSPECIFIED,
    UNINTERRUPTED
}