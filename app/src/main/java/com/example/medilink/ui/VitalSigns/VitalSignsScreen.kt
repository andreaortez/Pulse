package com.example.medilink.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink.ui.theme.Azul
import com.example.medilink.ui.theme.AzulNegro
import com.example.medilink.ui.theme.AzulOscuro
import com.example.medilink.ui.theme.CelesteVivido
import androidx.compose.material3.OutlinedTextFieldDefaults

//imports para monitoreo de salud
import androidx.compose.runtime.LaunchedEffect
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.temporal.ChronoUnit
import android.os.Build
import android.util.Log
import com.example.medilink.BuildConfig
import com.example.medilink.SessionManager
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalSignsScreen(
    onBackClick: () -> Unit = {},
    onChatbotClick: (bpm: String, pressure: String, temperature: String) -> Unit = { _, _, _ -> },
    idUsuario: String
) {
    Surface(                        
        modifier = Modifier.fillMaxSize(),
        color = Color.White){
    var bpm by remember { mutableStateOf("72") }
    var pressure by remember { mutableStateOf("120/80") }
    var temperature by remember { mutableStateOf("36.5") }

    val context = LocalContext.current
    LaunchedEffect(Unit){
        bpm = "999";
        Log.d("VitalSigns", "LLEGUE 0")
        val sdkStatus = HealthConnectClient.getSdkStatus(
            context,
            "com.google.android.apps.healthdata"
        )

        if (sdkStatus == HealthConnectClient.SDK_AVAILABLE && !(Build.VERSION.SDK_INT < Build.VERSION_CODES.O)) {
            if (!(Build.VERSION.SDK_INT < Build.VERSION_CODES.O)) {
                val client = HealthConnectClient.getOrCreate(context);

                //consiguiendo timeframes
                val now = Instant.now();

                val time = TimeRangeFilter.after(
                    now.minus(1, ChronoUnit.MINUTES)
                )

                try{
                    val bpm_report = client.readRecords(
                        ReadRecordsRequest(
                            recordType = HeartRateRecord::class,
                            timeRangeFilter = time
                        )
                    );

                    val latestBPM = bpm_report.records.maxByOrNull { it.startTime }
                    val latestBpmSample = latestBPM?.samples?.maxByOrNull { it.time }

                    latestBpmSample?.beatsPerMinute?.toInt()?.let { bpmValue ->
                        bpm = bpmValue.toString()
                    }

                    val pressure_report = client.readRecords(
                        ReadRecordsRequest(
                            recordType = BloodPressureRecord::class,
                            timeRangeFilter = time
                        )
                    );
                    val latestBP = pressure_report.records.maxByOrNull { it.time }
                    var num = latestBP?.systolic?.inMillimetersOfMercury?.toInt();
                    var den = latestBP?.diastolic?.inMillimetersOfMercury?.toInt();
                    if(num != null && den != null){
                        pressure = Integer.toString(num)+ "/" + Integer.toString(den);
                    }
                    val temperature_report = client.readRecords(
                        ReadRecordsRequest(
                            recordType = BodyTemperatureRecord::class,
                            timeRangeFilter = time
                        )
                    );
                    val latestTemp = temperature_report.records.maxByOrNull { it.time }?.temperature?.inCelsius


                    latestTemp ?.let { temp ->
                        temperature = String.format("%.1f", temp)
                    }

                    //escribir dentro de la tabla de registros


                }catch(e : Exception){

                }
            }

        }else{
            //generar los datos aleatorios para la simulación
            val url = URL(BuildConfig.VITALS_URL + "/simulate")
            Log.d("VitalSigns", "LLEGUE 1")
            val bodyJson = JSONObject().apply {}

            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 10_000
                readTimeout = 10_000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
            }
            Log.d("VitalSigns", "LLEGUE 2")
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
            Log.d("VitalSigns", "LLEGUE 3")
            try {
                val json = JSONObject(responseText)

                val bpmValue = json.optInt("bpm", -1)
                if (bpmValue != -1) {
                    bpm = bpmValue.toString()
                }

                val presionValue = json.optString("presion", "")
                if (presionValue.isNotBlank()) {
                    pressure = presionValue
                }

                val tempValue = json.optDouble("temperatura", Double.NaN)
                if (!tempValue.isNaN()) {
                    temperature = String.format("%.1f", tempValue)
                }

                Log.d("VitalSigns", "LLEGUE 4")
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }

        //escribir los datos como nuevo registros

        val bodyJson = JSONObject().apply {
            put("adultoMayorId", idUsuario)
            put("bpm", bpm)
            put("temperatura", temperature)
            put("presion", pressure)
        }

        val url = URL(BuildConfig.VITALS_URL + "/")
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
        val responseText = if (responseCode in 200..299) {
            conn.inputStream.bufferedReader().use { it.readText() }
        } else {
            conn.errorStream?.bufferedReader()?.use { it.readText() }
                ?: "Error HTTP $responseCode"
        }
        conn.disconnect()
    }
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2196F3),
            Color(0xFF1565C0)
        )
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradient)
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Signos vitales",
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
            Column(
                modifier = Modifier
                    .fillMaxWidth().background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {



                // Botón para análisis por chatbot
                OutlinedButton(
                    onClick = {
                        if (bpm.isBlank() || pressure.isBlank() || temperature.isBlank()) {
                            Toast.makeText(
                                context,
                                "Completa los campos antes de analizar",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            onChatbotClick(bpm, pressure, temperature)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = AzulOscuro,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "Análisis chatbot"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Analizar con chatbot",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            // Tarjeta estilo dashboard (Resumen)
            SummaryCard(
                bpm = bpm,
                pressure = pressure,
                temperature = temperature
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Bienvenido! Estos son sus signos vitales",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = AzulNegro,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Card BPM
            VitalInputCard(
                title = "Frecuencia cardíaca",
                subtitle = "Latidos por minuto",
                icon = Icons.Default.Favorite,
                iconTint = Color(0xFFE53935),
                value = bpm,
                unit = "bpm",
                keyboardType = KeyboardType.Number,
                onValueChange = { value ->
                    if (value.all { it.isDigit() } || value.isBlank()) {
                        bpm = value
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Card presión arterial
            VitalInputCard(
                title = "Presión arterial",
                subtitle = "Sistólica / diastólica",
                icon = Icons.Default.MonitorHeart,
                iconTint = Color(0xFF43A047),
                value = pressure,
                unit = "mmHg",
                keyboardType = KeyboardType.Text,
                onValueChange = { value ->
                    if (value.all { it.isDigit() || it == '/' } || value.isBlank()) {
                        pressure = value
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Card temperatura
            VitalInputCard(
                title = "Temperatura corporal",
                subtitle = "En grados Celsius",
                icon = Icons.Default.Thermostat,
                iconTint = Color(0xFFFFA000),
                value = temperature,
                unit = "°C",
                keyboardType = KeyboardType.Number,
                onValueChange = { value ->
                    if (value.matches(Regex("""\d*\.?\d*""")) || value.isBlank()) {
                        temperature = value
                    }
                },
                showTrailingUnitChip = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Los valores mostrados son una referencia general y no sustituyen la evaluación médica.",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}}

@Composable
private fun SummaryCard(
    bpm: String,
    pressure: String,
    temperature: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Resumen de hoy",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    label = "Frecuencia",
                    value = if (bpm.isNotBlank()) bpm else "--",
                    unit = "bpm"
                )
                SummaryItem(
                    label = "Presión",
                    value = if (pressure.isNotBlank()) pressure else "--",
                    unit = "mmHg"
                )
                SummaryItem(
                    label = "Temperatura",
                    value = if (temperature.isNotBlank()) temperature else "--",
                    unit = "°C"
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    unit: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AzulNegro
        )
        Text(
            text = unit,
            fontSize = 12.sp,
            color = Color.DarkGray
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VitalInputCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    value: String,
    unit: String,
    keyboardType: KeyboardType,
    onValueChange: (String) -> Unit,
    showTrailingUnitChip: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            color = iconTint.copy(alpha = 0.12f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AzulNegro
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                placeholder = { Text("Ingresar valor") },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = keyboardType
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Azul,
                    unfocusedBorderColor = Color.LightGray,
                    errorBorderColor = Color.Red,
                    cursorColor = Azul,
                    focusedLabelColor = Azul,
                    focusedTextColor = AzulNegro
                ),
                trailingIcon = {
                    if (showTrailingUnitChip) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(85.dp).height(53.dp)
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
                                text = unit,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        Text(
                            text = unit,
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VitalSignsScreenPreview() {
    MaterialTheme {
        VitalSignsScreen(idUsuario = "previewUser")
    }
}
