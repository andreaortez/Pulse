package com.example.medilink.ui


import com.example.medilink.BuildConfig
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextFieldDefaults

//components
import com.example.medilink.ui.components.MedicineFormChip
import com.example.medilink.ui.components.TimeReminderCard
import com.example.medilink.ui.components.DatePickerRange
import com.example.medilink.ui.components.DialTimePicker

// Colores
import com.example.medilink.ui.theme.CelesteVivido
import com.example.medilink.ui.theme.Azul
import com.example.medilink.ui.theme.AzulNegro
import com.example.medilink.ui.theme.AzulOscuro
import org.json.JSONArray
import java.lang.System.console
import java.text.SimpleDateFormat
import java.util.Locale

data class Usuario(
    val id: String,
    val nombre: String,
    val apellido: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    onBackClick: () -> Unit = {},
    onDoneClick: () -> Unit = {},
    idUsuario: String,
    type: String,
    existingMedicine: MedicineUi? = null,
    oldMedId: String? = null
) {
    val medsUrl = BuildConfig.MEDS_URL
    val usersUrl = BuildConfig.USERS_URL

    var medicineName by remember(existingMedicine) {
        mutableStateOf(existingMedicine?.name ?: "")
    }
    var amount by remember { mutableStateOf("1") }
    var selectedForm by remember { mutableStateOf(0) }
    var showTimePicker by remember { mutableStateOf(false) }

    var startDateDisplay by remember { mutableStateOf("") }
    var endDateDisplay by remember { mutableStateOf("") }
    var startDateBackend by remember { mutableStateOf("") }
    var endDateBackend by remember { mutableStateOf("") }

    var adultosMayores by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var adultoSeleccionado by remember { mutableStateOf<Usuario?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val targetUserId = if (type == "FAMILIAR") {
        adultoSeleccionado!!.id
    } else {
        idUsuario
    }

    if (type == "FAMILIAR") {
        LaunchedEffect(idUsuario) {
            adultosMayores = buscarAdultos(usersUrl, idUsuario)
        }
    }

    // HORAS DE RECORDATORIO
    val reminderTimes = remember { mutableStateListOf<String>() }

    val formas = listOf("Cápsula", "Tableta", "Solución", "Gotas")
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val medicineIcons = listOf(
        "Cápsula" to R.drawable.ic_capsule,
        "Tableta" to R.drawable.ic_tablet,
        "Solución" to R.drawable.ic_solution,
        "Gotas" to R.drawable.ic_gutte
    )

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2196F3),
            Color(0xFF1565C0)
        )
    )
    LaunchedEffect(existingMedicine?.id) {
        existingMedicine?.let { med ->
            medicineName = med.name
            amount = med.quantity.toString()


            startDateBackend = med.fechaInicio
            endDateBackend = med.fechaFin
            startDateDisplay = med.fechaInicio
            endDateDisplay = med.fechaFin


            reminderTimes.clear()
            reminderTimes.addAll(med.horas)

            selectedForm = formas.indexOf(med.forma).coerceAtLeast(0)
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
                            text = if (existingMedicine == null)
                                "Añadir Medicamento"
                            else
                                "Editar Medicamento",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        },
        bottomBar = {
            Button(
                onClick = {
                    scope.launch {
                        if (medicineName.isBlank()
                            || startDateBackend.isBlank()
                            || endDateBackend.isBlank()
                            || reminderTimes.isEmpty()
                        ) {
                            Toast.makeText(
                                context,
                                "Completa nombre, fechas y al menos una hora",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }


                        if (type == "FAMILIAR" && adultoSeleccionado == null) {
                            Toast.makeText(
                                context,
                                "Selecciona un adulto mayor",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }
                        val success = if (existingMedicine == null) {
                            createMedicine(
                                baseUrl = "$medsUrl/registerMed",
                                nombre = medicineName,
                                cantidad = amount,
                                tipo = formas[selectedForm],
                                fechaInicio = startDateBackend,
                                fechaFin = endDateBackend,
                                horas = reminderTimes.toList(),
                                idUsuario = targetUserId

                            )



                        } else {
                            println("MODIFY URL => $medsUrl/modifyMed")
                            println("MODIFY ID  => $oldMedId")

                            println("EDIT existingMedicine.id=${existingMedicine?.id} oldMedId=$oldMedId")


                            modifyMedicine(
                                baseUrl = "$medsUrl/modifyMed",
                                medId = existingMedicine.id,
                                nombre = medicineName,
                                cantidad = amount,
                                tipo = formas[selectedForm],
                                fechaInicio = startDateBackend,
                                fechaFin = endDateBackend,
                                horas = reminderTimes.toList(),
                                idUsuario = targetUserId
                            )
                        }


                        if (success) {
                            Toast.makeText(
                                context,
                                if (existingMedicine == null)
                                    "Medicamento guardado"
                                else
                                    "Cambios guardados",
                                Toast.LENGTH_SHORT
                            ).show()
                            onDoneClick()
                        } else {
                            Toast.makeText(
                                context,
                                "Error al guardar",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(55.dp)
                    .background(gradient, shape = RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    text = if (existingMedicine == null) "Añadir" else "Guardar",
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Text(
                text = "Nombre del medicamento",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = medicineName,
                onValueChange = { medicineName = it },
                placeholder = { Text("Ibuprofeno, 200 mg") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Azul,
                    unfocusedBorderColor = Color.LightGray,
                    errorBorderColor = Color.Red,
                    cursorColor = Azul,
                    focusedLabelColor = Azul,
                    focusedTextColor = AzulNegro
                )
            )

            // CANTIDAD
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 24.dp),
            ) {
                Text(
                    text = "Cantidad",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = Color.DarkGray,
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = { Text(amount) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Azul,
                        unfocusedBorderColor = Color.LightGray,
                        errorBorderColor = Color.Red,
                        cursorColor = Azul,
                        focusedLabelColor = Azul,
                        focusedTextColor = AzulNegro
                    ),
                    trailingIcon = {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(85.dp)
                                .background(
                                    color = CelesteVivido,
                                    shape = RoundedCornerShape(
                                        topEnd = 12.dp,
                                        bottomEnd = 12.dp
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "pastilla(s)",
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                    }
                )
            }

            if(type == "FAMILIAR") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                ) {
                    Text(
                        text = "Adulto Mayor",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = Color.DarkGray,
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = adultoSeleccionado?.let { "${it.nombre} ${it.apellido}" } ?: "",
                            onValueChange = { },
                            placeholder = { Text("Selecciona un adulto mayor") },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Azul,
                                unfocusedBorderColor = Color.LightGray,
                                cursorColor = Azul,
                                focusedLabelColor = Azul,
                                focusedTextColor = AzulNegro
                            ),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            singleLine = true
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            containerColor = Color.White,
                            onDismissRequest = { expanded = false }
                        ) {
                            adultosMayores.forEach { adulto ->
                                DropdownMenuItem(
                                    modifier = Modifier.background(Color.White),
                                    text = {
                                        Text(
                                            text = "${adulto.nombre} ${adulto.apellido}",
                                            color = AzulNegro
                                        )
                                    },
                                    onClick = {
                                        adultoSeleccionado = adulto
                                        expanded = false
                                    }
                                )
                            }
                        }

                    }
                }
            }

            Text(
                text = "Tipo de medicamento",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color.DarkGray,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                medicineIcons.forEachIndexed { index, (form, iconRes) ->
                    val isSelected = selectedForm == index

                    Box(modifier = Modifier.weight(1f)) {
                        MedicineFormChip(
                            text = form,
                            iconRes = iconRes,
                            selected = isSelected,
                            onClick = { selectedForm = index }
                        )
                    }
                }
            }

            DatePickerRange(
                startDateDisplay = startDateDisplay,
                endDateDisplay = endDateDisplay
            ) { startDisp, endDisp, startBack, endBack ->
                startDateDisplay = startDisp
                endDateDisplay = endDisp
                startDateBackend = startBack
                endDateBackend = endBack
            }


            Text(
                text = "Añadir Recordatorios",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedButton(
                onClick = { showTimePicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = AzulOscuro,
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Seleccionar hora",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Añadir recordatorio",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (showTimePicker) {
                DialTimePicker(
                    onConfirm = { timePickerState ->
                        val hh = timePickerState.hour.toString().padStart(2, '0')
                        val mm = timePickerState.minute.toString().padStart(2, '0')
                        val newTime = "$hh:$mm"

                        if (!reminderTimes.contains(newTime)) {
                            reminderTimes.add(newTime)
                        }

                        showTimePicker = false
                    },
                    onDismiss = {
                        showTimePicker = false
                    }
                )
            }

            if (reminderTimes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Recordatorio",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp),
                        color = Color.DarkGray,
                    )
                    reminderTimes.forEach { t ->
                        TimeReminderCard(
                            time = t,
                            onDeleteClick = {
                                reminderTimes.remove(t)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddMedicineScreenPreview() {
    MaterialTheme {
        AddMedicineScreen(
            idUsuario = "previewUser",
            type = "FAMILIAR",
            existingMedicine = null,
            oldMedId = null
        )
    }
}

suspend fun createMedicine(
    baseUrl: String,
    nombre: String,
    cantidad: String,
    tipo: String,
    fechaInicio: String,
    fechaFin: String,
    horas: List<String>,
    idUsuario: String
): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val cantInt = cantidad.toIntOrNull() ?: 1

            val bodyJson = JSONObject().apply {
                put("nombre", nombre)
                put("cantidad", cantInt)
                put("forma", tipo)
                put("fecha_inicio", fechaInicio)
                put("fecha_fin", fechaFin)
                put("horas_recordatorio", JSONArray(horas))
                put("id_usuario", idUsuario)
            }

            val url = URL(baseUrl)
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
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                conn.disconnect()

                val json = JSONObject(responseText)
                val medicamentoJson = json.optJSONObject("medicamento")
                val medId = medicamentoJson?.optString("_id")

                // 2) Crear recordatorio inicial usando la primera hora
                val primeraHora = horas.firstOrNull()

                if (!medId.isNullOrBlank() && !primeraHora.isNullOrBlank()) {
                    try {
                        // Combinar fechaInicio ("yyyy-MM-dd") + primeraHora ("HH:mm")
                        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        val date = parser.parse("$fechaInicio $primeraHora")

                        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        val fechaIso = isoFormatter.format(date!!)

                        val reminderUrlStr = baseUrl.replace("/registerMed", "/agregarRecordatorio")
                        val reminderUrl = URL(reminderUrlStr)

                        val bodyRecordatorio = JSONObject().apply {
                            put("med_id", medId)
                            put("fecha", fechaIso)
                        }

                        val connRem = (reminderUrl.openConnection() as HttpURLConnection).apply {
                            requestMethod = "POST"
                            connectTimeout = 10_000
                            readTimeout = 10_000
                            doOutput = true
                            setRequestProperty("Content-Type", "application/json; charset=utf-8")
                        }

                        connRem.outputStream.use { os ->
                            val input = bodyRecordatorio.toString().toByteArray(Charsets.UTF_8)
                            os.write(input, 0, input.size)
                        }

                        val reminderCode = connRem.responseCode
                        connRem.disconnect()

                        println("Recordatorio creado, status: $reminderCode")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                true
            } else {
                // Error en creación de medicamento
                val errorText = conn.errorStream?.bufferedReader()?.use { it.readText() }
                conn.disconnect()
                println("Error creando medicamento: $errorText")
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
suspend fun modifyMedicine(
    baseUrl: String,
    medId: String,
    nombre: String,
    cantidad: String,
    tipo: String,
    fechaInicio: String,
    fechaFin: String,
    horas: List<String>,
    idUsuario: String
): Boolean = withContext(Dispatchers.IO) {
    try {
        val cantInt = cantidad.toIntOrNull() ?: 1

        val bodyJson = JSONObject().apply {
            put("_id", medId)
            put("nombre", nombre)
            put("cantidad", cantInt)
            put("forma", tipo)
            put("fecha_inicio", fechaInicio)
            put("fecha_fin", fechaFin)
            put("horas_recordatorio", JSONArray(horas))
            put("id_usuario", idUsuario)
        }

        val conn = (URL(baseUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }

        conn.outputStream.use {
            it.write(bodyJson.toString().toByteArray(Charsets.UTF_8))
        }

        val code = conn.responseCode
        val body = if (code in 200..299) {
            conn.inputStream.bufferedReader().readText()
        } else {
            conn.errorStream?.bufferedReader()?.readText()
        }

        println("MODIFY responseCode=$code body=$body")

        conn.disconnect()
        code in 200..299
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}


suspend fun deleteMedicineById(
    baseUrl: String,
    medId: String
): Boolean = withContext(Dispatchers.IO) {
    try {
        val url = URL(baseUrl)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "DELETE"
            connectTimeout = 10_000
            readTimeout = 10_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        }

        val body = JSONObject().put("_id", medId).toString()
        conn.outputStream.use { os ->
            os.write(body.toByteArray(Charsets.UTF_8))
        }

        val code = conn.responseCode
        conn.disconnect()
        code in 200..299
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}


suspend fun buscarAdultos(
    baseUrl: String,
    id: String
): List<Usuario> {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/$id/adultos-mayores")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
            }

            val responseCode = conn.responseCode

            if (responseCode in 200..299) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                conn.disconnect()

                val root = JSONObject(responseText)

                val adultosArray = root.optJSONArray("adultosMayores")
                    ?: return@withContext emptyList<Usuario>()

                val resultado = mutableListOf<Usuario>()

                for (i in 0 until adultosArray.length()) {
                    val item = adultosArray.getJSONObject(i)
                    resultado.add(
                        Usuario(
                            id = item.optString("_id", ""),
                            nombre = item.optString("nombre", ""),
                            apellido = item.optString("apellido", "")
                        )
                    )
                }

                resultado
            } else {
                val errorText = conn.errorStream?.bufferedReader()?.use { it.readText() }
                conn.disconnect()
                println("Error get adultos-mayores: $errorText")
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}