package com.example.medilink.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

data class ChatRequest(
    @SerializedName("user_prompt")
    val userPrompt: String
)

data class ChatBotData(
    val respuesta: String?,
    val consejos: List<String>?
)

data class ChatResponse(
    val message: String,
    val data: ChatBotData?
)

interface ChatApi {

    // POST http://10.0.2.2:3000/chatbot/request
    @POST("chatbot/request")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
}
