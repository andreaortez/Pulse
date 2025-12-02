package com.example.medilink.ui.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// Colores de tu paleta
import com.example.medilink.ui.theme.AzulNegro
import com.example.medilink.ui.theme.CelesteVivido
import com.example.medilink.ui.theme.CelesteClaro

data class UsuarioAfiliado(
    val id: String,
    val nombre: String,
    val apellido: String,
    val tipo: String,
    val correo: String,
    val telefono: String? = null,
    val edad: Int? = null,
    val fechaVinculacion: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarUsuariosScreen(
    idUsuarioActual: String,
    tipoUsuarioActual: String,
    onBackClick: () -> Unit = {},
    onAgregarAfiliado: () -> Unit = {},
    onVerDetalle: (String) -> Unit = {}
) {
    var afiliados by remember { mutableStateOf<List<UsuarioAfiliado>>(emptyList()) }
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
                actions = {
                    IconButton(
                        onClick = onAgregarAfiliado,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Agregar afiliado"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAgregarAfiliado,
                modifier = Modifier.padding(bottom = 16.dp, end = 16.dp),
                containerColor = CelesteVivido,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Agregar",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Vincular Persona")
            }
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
                            text = "${afiliados.size} personas",
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

            // Filtros
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("TODOS", "FAMILIAR", "ADULTO_MAYOR").forEach { filtro ->
                    val isSelected = selectedFilter == filtro

                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filtro },
                        label = {
                            Text(
                                text = filtro.replace("_", " "),
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CelesteVivido,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = AzulNegro
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color.LightGray,
                            selectedBorderColor = CelesteVivido,
                            borderWidth = 1.dp
                        ),
                        modifier = Modifier.height(32.dp)
                    )
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
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Presiona el botón + para vincular personas",
                                color = AzulNegro.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onAgregarAfiliado,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CelesteVivido,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Vincular Ahora")
                            }
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
                                AfiliadoCard(
                                    usuario = afiliado,
                                    onClick = { onVerDetalle(afiliado.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AfiliadoCard(
    usuario: UsuarioAfiliado,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar/Inicial
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        if (usuario.tipo.contains("ADULTO_MAYOR", ignoreCase = true)) {
                            CelesteVivido.copy(alpha = 0.2f)
                        } else {
                            AzulNegro.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = usuario.nombre.first().uppercase(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (usuario.tipo.contains("ADULTO_MAYOR", ignoreCase = true)) {
                            CelesteVivido
                        } else {
                            AzulNegro
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información principal
            // Información principal
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${usuario.nombre} ${usuario.apellido}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = AzulNegro
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // "Chip" de tipo de usuario
                    val esAdultoMayor = usuario.tipo.contains("ADULTO_MAYOR", ignoreCase = true)

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
                            text = usuario.tipo.replace("_", " "),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = chipTextColor
                        )
                    }
                }
            }

            // Icono de acción
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Ver detalles",
                modifier = Modifier.size(24.dp),
                tint = AzulNegro.copy(alpha = 0.5f)
            )
        }
    }
}

suspend fun cargarAfiliados(
    baseUrl: String,
    idUsuarioActual: String,
    tipoUsuarioActual: String,
    onResult: (List<UsuarioAfiliado>, String?) -> Unit
) = withContext(Dispatchers.IO) {
    try {
        val afiliados = mutableListOf<UsuarioAfiliado>()

        // Determinar qué endpoint usar según el tipo de usuario
        val endpoint = if (tipoUsuarioActual.contains("FAMILIAR", ignoreCase = true)) {
            "$baseUrl/$idUsuarioActual/adultos-mayores"
        } else {
            "$baseUrl/$idUsuarioActual/encargados"
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
            val key = if (tipoUsuarioActual.contains("FAMILIAR", ignoreCase = true)) {
                "adultosMayores"
            } else {
                "encargados"
            }

            val jsonArray = json.optJSONArray(key) ?: JSONArray()

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                afiliados.add(
                    UsuarioAfiliado(
                        id = item.optString("_id", ""),
                        nombre = item.optString("nombre", ""),
                        apellido = item.optString("apellido", ""),
                        tipo = item.optString("tipoUsuario", ""),
                        correo = item.optString("correo", ""),
                        telefono = item.optString("num_telefono", null),
                        edad = item.optInt("edad", 0).takeIf { it > 0 }
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