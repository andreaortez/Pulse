package com.example.medilink.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink.BuildConfig
import com.example.medilink.data.ApiClient
import com.example.medilink.data.ChatRequest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ChatBotActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bpm = intent.getStringExtra(BPM)
        val pressure = intent.getStringExtra(PRESSURE)
        val temperature = intent.getStringExtra(TEMPERATURE)
        setContent {
            MaterialTheme {
                ChatBotScreen(onBackClick = { finish() },
                    initialBpm = bpm,
                    initialPressure = pressure,
                    initialTemperature = temperature)
            }
        }
    }
    companion object {
        const val BPM = "BPM"
        const val PRESSURE = "PRESSURE"
        const val TEMPERATURE = "TEMPERATURE"
    }
}

data class ChatMessage(
    val text: String,
    val fromUser: Boolean,
    val isTyping: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(onBackClick: () -> Unit,
                  initialBpm: String? = null,
                  initialPressure: String? = null,
                  initialTemperature: String? = null) {


    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2196F3),
            Color(0xFF1565C0)
        )
    )

    val scope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                text = "Hola, soy tu asistente de salud. Puedo ayudarte a registrar o entender tus signos vitales 😊",
                fromUser = false
            )
        )
    }

    //generación de analisis en caso de que se llame desde el sensor
    LaunchedEffect(Unit) {
        if (!initialBpm.isNullOrBlank()
            && !initialPressure.isNullOrBlank()
            && !initialTemperature.isNullOrBlank()
        ) {

            val typingMessage = ChatMessage(
                text = "Escribiendo...",
                fromUser = false,
                isTyping = true
            )
            messages.add(typingMessage)

            val resultText = withContext(Dispatchers.IO) {
                try {

                    val bodyJson = JSONObject().apply {
                        put("bpm", initialBpm)
                        put("temperatura_corporal", initialTemperature)
                        put("presion_arterial", initialPressure)
                    }
                    print(BuildConfig.CHATBOT_URL + "/generateHealthReport")
                    val url = URL(BuildConfig.CHATBOT_URL + "/generateHealthReport")
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        connectTimeout = 10_000
                        readTimeout = 30_000
                        doOutput = true
                        setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    }

                    conn.outputStream.use { os ->
                        val input = bodyJson.toString().toByteArray(Charsets.UTF_8)
                        os.write(input, 0, input.size)
                    }

                    val responseCode = conn.responseCode
                    val responseText = if (responseCode in 200..299) {
                        conn.inputStream.bufferedReader().use { it.readText() }
                    } else {
                        conn.errorStream?.bufferedReader()?.use { it.readText() }
                            ?: "Error HTTP $responseCode"
                    }
                    conn.disconnect()

                    val json = JSONObject(responseText)
                    json.optString("reporte", responseText)
                } catch (e: Exception) {
                    e.printStackTrace()
                    "Lo siento, hubo un error al analizar tus signos vitales 😔"
                }
            }

            val index = messages.indexOf(typingMessage)
            if (index != -1) {
                messages[index] = ChatMessage(
                    text = resultText,
                    fromUser = false,
                    isTyping = false
                )
            } else {
                messages.add(
                    ChatMessage(
                        text = resultText,
                        fromUser = false,
                        isTyping = false
                    )
                )
            }
        }
    }


    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradient)
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Asistente de salud",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        },
        bottomBar = {
            ChatInputBar(
                text = inputText,
                onTextChange = { inputText = it },
                onSendClick = {
                    if (inputText.isNotBlank()) {
                        val userMessage = inputText

                        // 1) Agregar mensaje del usuario
                        messages.add(ChatMessage(userMessage, fromUser = true))
                        inputText = ""

                        // 2) Agregar burbuja de "escribiendo..."
                        val typingMessage = ChatMessage(
                            text = "Escribiendo...",
                            fromUser = false,
                            isTyping = true
                        )
                        messages.add(typingMessage)

                        scope.launch {
                            try {
                                val response = ApiClient.chatApi.sendMessage(
                                    ChatRequest(userPrompt = userMessage)
                                )

                                val botText = response.data?.respuesta
                                    ?: response.message
                                    ?: "No se pudo generar una respuesta en este momento."

                                // 3) Reemplazar la burbuja de typing por la respuesta real
                                val index = messages.indexOf(typingMessage)
                                if (index != -1) {
                                    messages[index] = ChatMessage(
                                        text = botText,
                                        fromUser = false
                                    )
                                } else {
                                    messages.add(
                                        ChatMessage(
                                            text = botText,
                                            fromUser = false
                                        )
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()

                                val errorText =
                                    "Lo siento, hubo un error al conectarme al servidor 😔"

                                val index = messages.indexOf(typingMessage)
                                if (index != -1) {
                                    messages[index] = ChatMessage(
                                        text = errorText,
                                        fromUser = false
                                    )
                                } else {
                                    messages.add(
                                        ChatMessage(
                                            text = errorText,
                                            fromUser = false
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            )

        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF6F2FA))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(message = msg)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val bubbleColor =
        if (message.fromUser) Color(0xFF2196F3) else Color.White
    val textColor =
        if (message.fromUser) Color.White else Color(0xFF012248)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.fromUser)
            Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = bubbleColor,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(
                text = if (message.isTyping) "•••" else message.text,
                color = if (message.isTyping) Color(0xFF7A7A7A) else textColor,
                fontSize = if (message.isTyping) 18.sp else 17.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 50.dp),
                placeholder = {
                    Text(
                        "Escribe tu pregunta...",
                        color = Color(0xFF7A7A7A),
                        fontSize = 16.sp
                    )
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color(0xFF012248)
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF012248),
                    unfocusedBorderColor = Color(0xFF012248),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = Color(0xFF012248)
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                singleLine = false
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSendClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFF2196F3),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Enviar",
                    tint = Color.White
                )
            }
        }
    }
}
