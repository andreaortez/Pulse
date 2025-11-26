package com.example.medilink.ui


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.medilink.ui.Components.MedicineFormChip
import com.example.medilink.ui.Components.TimeReminderCard
import com.example.medilink.ui.Components.DatePickerRange
import com.example.medilink.ui.theme.CelesteVivido
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    onBackClick: () -> Unit = {},
    onDoneClick: () -> Unit = {},
    idUsuario: String = "671234abcd1234abcd1234ff",
    existingMedicine: MedicineUi? = null
) {
    // URL base de tu backend (ajústala)
    val BASE_URL = "https://Backend/meds"

    var medicineName by remember(existingMedicine) {
        mutableStateOf(existingMedicine?.name ?: "")
    }
    var amount by remember { mutableStateOf("1") }
    var duration by remember { mutableStateOf("2") } // si luego no lo usas, lo puedes eliminar
    var selectedForm by remember { mutableStateOf(0) }

    // FECHAS (display y formato backend)
    val dateFormatDisplay = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val dateFormatBackend = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    var startDateDisplay by remember { mutableStateOf("") }
    var endDateDisplay by remember { mutableStateOf("") }
    var startDateBackend by remember { mutableStateOf("") }
    var endDateBackend by remember { mutableStateOf("") }

    // HORAS DE RECORDATORIO (lista)
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

    // Helpers para abrir DatePicker y TimePicker
    fun pickDate(onPicked: (display: String, backend: String) -> Unit) {
        val c = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                val display = dateFormatDisplay.format(cal.time)
                val backend = dateFormatBackend.format(cal.time)
                onPicked(display, backend)
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun pickTime(onPicked: (String) -> Unit) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, h, m ->
                val hh = h.toString().padStart(2, '0')
                val mm = m.toString().padStart(2, '0')
                onPicked("$hh:$mm") // formato "HH:mm"
            },
            hour,
            minute,
            true // 24h
        ).show()
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

                        val success = createMedicine(
                            baseUrl = "$BASE_URL/registerMed",
                            nombre = medicineName,
                            cantidad = amount,
                            forma = formas[selectedForm],
                            fechaInicio = startDateBackend,
                            fechaFin = endDateBackend,
                            horas = reminderTimes.toList(),
                            idUsuario = idUsuario
                        )

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
        }
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
                shape = RoundedCornerShape(12.dp)
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
                text = "Horas de recordatorio",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TimeReminderCard(
                label = "Agregar hora",
                time = if (reminderTimes.isEmpty())
                    "Toca + para añadir"
                else
                    reminderTimes.joinToString(", "),
                onAddClick = {
                    pickTime { newTime ->
                        if (!reminderTimes.contains(newTime)) {
                            reminderTimes.add(newTime)
                        }
                    }
                }
            )

            if (reminderTimes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    reminderTimes.forEach { t ->
                        AssistChip(
                            onClick = { /* podrías borrar o editar si quieres */ },
                            label = { Text(t) }
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
        AddMedicineScreen()
    }
}

// crear medicamento AHORA usando fecha_inicio, fecha_fin y lista de horas
suspend fun createMedicine(
    baseUrl: String,
    nombre: String,
    cantidad: String,
    forma: String,
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
                put("forma", forma)
                put("fecha_inicio", fechaInicio)
                put("fecha_fin", fechaFin)
                put("horas_recordatorio", horas)
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
            conn.disconnect()

            responseCode in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
