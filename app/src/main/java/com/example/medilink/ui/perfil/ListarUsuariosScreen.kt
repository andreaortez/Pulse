package com.example.medilink.ui.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medilink.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

//components
import com.example.medilink.ui.components.UserCard

// Colores
import com.example.medilink.ui.theme.AzulNegro
import com.example.medilink.ui.theme.CelesteVivido
import com.example.medilink.ui.theme.CelesteClaro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarUsuariosScreen(
    idUsuarioActual: String,
    tipoUsuarioActual: String,
    onBackClick: () -> Unit = {},
) {
    var afiliados by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedFilter by remember { mutableStateOf("TODOS") }

    val baseUrl = BuildConfig.USERS_URL
    val scope = rememberCoroutineScope()

    // Cargar afiliados al iniciar
    LaunchedEffect(idUsuarioActual, tipoUsuarioActual) {
        cargarAfiliados(baseUrl, idUsuarioActual, tipoUsuarioActual) { resultado, error ->
            afiliados = resultado
            errorMessage = error
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Afiliados",
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
                ),
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(CelesteClaro)
        ) {
            // Encabezado con estadísticas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = if (tipoUsuarioActual.contains("FAMILIAR")) {
                                "Adultos Mayores a tu Cargo"
                            } else {
                                "Familiares Encargados"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = AzulNegro.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${afiliados.size} persona(s)",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AzulNegro
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                CelesteVivido.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Afiliados",
                            modifier = Modifier.size(32.dp),
                            tint = CelesteVivido
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lista de afiliados
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                when {
                    isLoading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = CelesteVivido,
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Cargando afiliados...",
                                color = AzulNegro.copy(alpha = 0.7f)
                            )
                        }
                    }

                    errorMessage != null -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = errorMessage ?: "Error desconocido",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    isLoading = true
                                    errorMessage = null
                                    scope.launch {
                                        cargarAfiliados(baseUrl, idUsuarioActual, tipoUsuarioActual) { resultado, error ->
                                            afiliados = resultado
                                            errorMessage = error
                                            isLoading = false
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CelesteVivido,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }

                    //si no hay afiliados
                    afiliados.isEmpty() -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Sin afiliados",
                                modifier = Modifier.size(80.dp),
                                tint = AzulNegro.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (tipoUsuarioActual.contains("FAMILIAR")) {
                                    "No tienes adultos mayores a tu cargo"
                                } else {
                                    "No tienes familiares encargados"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = AzulNegro,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }

                    else -> {
                        val afiliadosFiltrados = afiliados.filter { afiliado ->
                            when (selectedFilter) {
                                "FAMILIAR" -> afiliado.tipo.contains("FAMILIAR", ignoreCase = true)
                                "ADULTO_MAYOR" -> afiliado.tipo.contains("ADULTO_MAYOR", ignoreCase = true)
                                else -> true
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            item {
                                Text(
                                    text = "Total: ${afiliadosFiltrados.size} persona(s)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AzulNegro.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            items(afiliadosFiltrados) { afiliado ->
                                UserCard(
                                    usuario = afiliado,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

suspend fun cargarAfiliados(
    baseUrl: String,
    id: String,
    tipo: String,
    onResult: (List<Usuario>, String?) -> Unit
) = withContext(Dispatchers.IO) {
    try {
        val afiliados = mutableListOf<Usuario>()

        // Determinar qué endpoint usar según el tipo de usuario
        val endpoint = if (tipo == "FAMILIAR") {
            "$baseUrl/$id/adultos-mayores"
        } else {
            "$baseUrl/$id/familiares"
        }

        val url = URL(endpoint)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 15_000
        }

        val responseCode = conn.responseCode

        if (responseCode in 200..299) {
            val responseText = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(responseText)

            // Extraer array según el tipo de usuario
            val key = if (tipo.contains("FAMILIAR", ignoreCase = true)) {
                "adultosMayores"
            } else {
                "familiares"
            }

            val jsonArray = json.optJSONArray(key) ?: JSONArray()

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                afiliados.add(
                    Usuario(
                        id = item.optString("_id", ""),
                        nombre = item.optString("nombre", ""),
                        apellido = item.optString("apellido", ""),
                        tipo = item.optString("tipoUsuario", ""),
                    )
                )
            }

            conn.disconnect()
            withContext(Dispatchers.Main) {
                onResult(afiliados.filter { it.id.isNotEmpty() && it.nombre.isNotEmpty() }, null)
            }
        } else {
            conn.disconnect()
            withContext(Dispatchers.Main) {
                onResult(emptyList(), "Error al cargar afiliados: $responseCode")
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        withContext(Dispatchers.Main) {
            onResult(emptyList(), "Error de conexión: ${e.message}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListarUsuariosScreenPreview() {
    MaterialTheme {
        ListarUsuariosScreen(
            idUsuarioActual = "",
            tipoUsuarioActual = "FAMILIAR",
            onBackClick = {},
        )
    }
}