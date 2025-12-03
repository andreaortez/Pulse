package com.example.medilink.ui.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.style.TextAlign

//Colores
import com.example.medilink.ui.theme.AzulNegro
import com.example.medilink.ui.theme.CelesteVivido
import com.example.medilink.ui.theme.CelesteClaro
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

data class Usuario(
    val id: String,
    val nombre: String,
    val apellido: String,
    val tipo: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VincularFamiliarScreen(
    idUsuarioActual: String,
    type: String,
    onBackClick: () -> Unit = {},
    onVincularExitoso: () -> Unit = {}
) {
    var idTexto by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val baseUrl = BuildConfig.USERS_URL
    val scope = rememberCoroutineScope()

    val vincular = when (type) {
        "FAMILIAR" -> "Vincular Adulto Mayor"
        "ADULTO_MAYOR" -> "Vincular Familiar"
        else -> "Vincular Usuario"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = vincular,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulNegro,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(CelesteClaro)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tarjeta de búsqueda
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icono decorativo
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(
                                CelesteVivido.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Vincular",
                            modifier = Modifier.size(36.dp),
                            tint = CelesteVivido
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Buscar Usuario",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = AzulNegro
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Ingresa el ID del familiar o adulto mayor que deseas vincular",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Campo de búsqueda
                    OutlinedTextField(
                        value = idTexto,
                        onValueChange = {
                            idTexto = it
                            mensaje = null
                        },
                        label = {
                            Text(
                                "ID del usuario",
                                color = AzulNegro.copy(alpha = 0.7f)
                            )
                        },
                        placeholder = {
                            Text("Ej: 507f1f77bcf86cd799439011")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = CelesteVivido
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CelesteVivido,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = CelesteVivido,
                            focusedLeadingIconColor = CelesteVivido,
                            cursorColor = CelesteVivido,
                            focusedTextColor = AzulNegro
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de búsqueda
                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                mensaje = null
                                isError = false
                                usuario = null

                                val resultado = buscarUsuario(
                                    baseUrl = baseUrl,
                                    id = idTexto.trim()
                                )

                                isLoading = false

                                if (resultado != null) {
                                    usuario = resultado
                                    mensaje = "Usuario encontrado"
                                    isError = false
                                } else {
                                    mensaje = "Usuario no encontrado. Verifica el ID."
                                    isError = true
                                }
                            }
                        },
                        enabled = idTexto.isNotBlank() && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CelesteVivido,
                            disabledContainerColor = CelesteVivido.copy(alpha = 0.5f),
                            contentColor = Color.White
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = "Buscar Usuario",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Mensajes de estado
            mensaje?.let { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isError) {
                            MaterialTheme.colorScheme.errorContainer
                        } else if (msg.contains("exitosa", ignoreCase = true)) {
                            CelesteVivido.copy(alpha = 0.1f)
                        } else {
                            CelesteVivido.copy(alpha = 0.1f)
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = msg,
                            color = if (isError) {
                                MaterialTheme.colorScheme.error
                            } else if (msg.contains("exitosa", ignoreCase = true)) {
                                CelesteVivido
                            } else {
                                AzulNegro
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tarjeta de usuario encontrado
            usuario?.let { user ->
                val esAdultoMayor = user.tipo.equals("ADULTO_MAYOR", ignoreCase = true)
                val tipoNormalizado = when (user.tipo.uppercase()) {
                    "ADULTO_MAYOR" -> "Adulto mayor"
                    "FAMILIAR"     -> "Familiar"
                    else -> user.tipo
                        .replace("_", " ")
                        .lowercase()
                        .replaceFirstChar { it.titlecase() }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Encabezado de usuario
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (esAdultoMayor) {
                                            CelesteVivido.copy(alpha = 0.2f)
                                        } else {
                                            AzulNegro.copy(alpha = 0.2f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.nombre.first().uppercase(),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (esAdultoMayor) {
                                            CelesteVivido
                                        } else {
                                            AzulNegro
                                        }
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${user.nombre} ${user.apellido}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = AzulNegro
                                    )
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                val chipBgColor = if (esAdultoMayor) {
                                    CelesteVivido.copy(alpha = 0.12f)
                                } else {
                                    AzulNegro.copy(alpha = 0.12f)
                                }

                                val chipTextColor = if (esAdultoMayor) {
                                    CelesteVivido
                                } else {
                                    AzulNegro
                                }

                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = chipBgColor,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = tipoNormalizado,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = chipTextColor
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Usuario",
                                modifier = Modifier.size(24.dp),
                                tint = AzulNegro.copy(alpha = 0.5f)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón de vincular (lo de antes, igual)
                        Button(
                            onClick = {
                                scope.launch {
                                    isLoading = true
                                    mensaje = null

                                    val usuarioActualEsFamiliar = type.contains("FAMILIAR", ignoreCase = true)
                                    val usuarioBuscadoEsFamiliar = user.tipo.contains("FAMILIAR", ignoreCase = true)

                                    if (usuarioActualEsFamiliar == usuarioBuscadoEsFamiliar) {
                                        mensaje = "Solo puedes vincular usuarios de tipo diferente:\n" +
                                                "Tú eres $type y estás intentando vincular a ${user.tipo}"
                                        isLoading = false
                                        return@launch
                                    }

                                    val ok = vincularUsuario(
                                        baseUrl = baseUrl,
                                        adultoMayorId = if (usuarioActualEsFamiliar) user.id else idUsuarioActual,
                                        familiarId   = if (usuarioActualEsFamiliar) idUsuarioActual else user.id
                                    )

                                    isLoading = false

                                    if (ok) {
                                        mensaje = "Vinculación exitosa!"
                                        isError = false
                                        usuario = null
                                        idTexto = ""

                                        scope.launch {
                                            delay(1500)
                                            onVincularExitoso()
                                        }
                                    } else {
                                        mensaje = "Error al vincular. Revisa los datos."
                                        isError = true
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AzulNegro,
                                disabledContainerColor = AzulNegro.copy(alpha = 0.5f),
                                contentColor = Color.White
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = "Vincular Usuario",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Instrucciones
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AzulNegro.copy(alpha = 0.05f)
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ℹ️ ¿Cómo funciona?",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AzulNegro
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "1. Un adulto mayor puede tener varios familiares encargados.\n" +
                                "2. Un familiar puede cuidar a varios adultos mayores.\n" +
                                "3. Solo se pueden vincular usuarios con tipos diferentes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AzulNegro.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

suspend fun buscarUsuario(
    baseUrl: String,
    id: String
): Usuario? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/$id")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
            }

            val responseCode = conn.responseCode

            if (responseCode in 200..299) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                conn.disconnect()

                val json = JSONObject(responseText)
                Usuario(
                    id = json.optString("_id", id),
                    nombre = json.optString("nombre", ""),
                    apellido = json.optString("apellido", ""),
                    tipo = json.optString("tipoUsuario", "")
                )
            } else {
                val errorText = conn.errorStream?.bufferedReader()?.use { it.readText() }
                conn.disconnect()
                println("Error getUserById: $errorText")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

suspend fun vincularUsuario(
    baseUrl: String,
    adultoMayorId: String,
    familiarId: String
): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val bodyJson = JSONObject().apply {
                put("id_usuario", adultoMayorId)
                put("id_familiar", familiarId)
            }

            val url = URL("$baseUrl/asignar-familiar")

            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 10_000
                readTimeout = 10_000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
            }

            conn.outputStream.use { os ->
                val input = bodyJson.toString().toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = conn.responseCode

            if (responseCode in 200..299) {
                conn.disconnect()
                true
            } else {
                val errorText = conn.errorStream?.bufferedReader()?.use { it.readText() }
                conn.disconnect()
                println("Error! No se pudo vincular: $errorText")
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
