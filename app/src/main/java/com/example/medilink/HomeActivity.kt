package com.example.medilink

import DayCalendarAdapter
import DayUi
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import com.example.medilink.ui.ChatBotActivity
import com.example.medilink.ui.MedicineUi
import com.example.medilink.ui.MedicinesAdapter
import com.example.medilink.ui.VitalSigns.VitalSignsActivity
import org.json.JSONArray
import android.widget.Toast
import androidx.appcompat.app.AlertDialog


data class Alert(
    val id: String,
    val mensaje: String,
    val gravedad: String,
    val estado: String
)

data class AdultoVinculado(
    val id: String,
    val nombre: String,
    val apellido: String
)

data class HomeReminder(
    val medId: String,
    val nombre: String,
    val proximoRecordatorioTexto: String,
    val usuarioId: String,
    val forma: String,
    val cantidad: Int
)

class HomeActivity : AppCompatActivity() {
    private lateinit var rvMedicines: RecyclerView
    private lateinit var tvEmptyMedicines: TextView
    private lateinit var medsBaseUrl: String
    private lateinit var usersBaseUrl: String
    private var selectedDateIso: String = ""

    private var selectedAdultId: String? = null
    private var userType: String = ""
    private var userId: String = ""

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        medsBaseUrl = BuildConfig.MEDS_URL
        usersBaseUrl = BuildConfig.USERS_URL

        userType = SessionManager.getUserType(this) ?: ""
        userId = SessionManager.getUserId(this) ?: ""

        //tabs
        val navUser = findViewById<ImageView>(R.id.navUser)
        navUser.setOnClickListener {
            val intent = Intent(this, MyProfileActivity::class.java)
            startActivity(intent)
        }

        //spinner
        val spinnerAdultos = findViewById<android.widget.Spinner>(R.id.spinnerAdultos)

        if (!userType.contains("FAMILIAR", ignoreCase = true)) {
            spinnerAdultos.visibility = View.GONE
        }

        val rvDays = findViewById<RecyclerView>(R.id.rvDays)
        val tvToday = findViewById<TextView>(R.id.tvToday)

        rvMedicines = findViewById<RecyclerView>(R.id.rvMedicines)
        rvMedicines.layoutManager = LinearLayoutManager(this)

        tvEmptyMedicines = findViewById(R.id.tvEmptyMedicines)

        val localeEs = Locale("es", "ES")
        val today = LocalDate.now()

        selectedDateIso = today.format(DateTimeFormatter.ISO_LOCAL_DATE)

        val navHeart = findViewById<ImageView>(R.id.navHeart)
        navHeart.setOnClickListener {
            val intent = Intent(this, ChatBotActivity::class.java)
            startActivity(intent)
        }
        tvToday.text = "Hoy, " + today.format(
            DateTimeFormatter.ofPattern("dd MMM", localeEs)
        )

        rvDays.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val days = listOf(
            DayUi(today.minusDays(2)),
            DayUi(today.minusDays(1)),
            DayUi(today),
            DayUi(today.plusDays(1)),
            DayUi(today.plusDays(2))
        )

        val centerIndex = 2

        val adapter = DayCalendarAdapter(
            days = days,
            onDaySelected = { selectedDate ->
                tvToday.text = "Hoy, " + selectedDate.format(
                    DateTimeFormatter.ofPattern("dd MMM", localeEs)
                )

                // 🔹 Actualizamos la fecha para el backend
                selectedDateIso = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

                lifecycleScope.launch {
                    val endpoint = if (userType.contains("FAMILIAR", ignoreCase = true)) {
                        "$medsBaseUrl/by-date-familiar"
                    } else {
                        "$medsBaseUrl/by-date"
                    }

                    val reminders = obtenerRecordatoriosHome(endpoint, userId, selectedDateIso)

                    val remindersFiltrados = if (userType.contains("FAMILIAR", ignoreCase = true)
                        && selectedAdultId != null
                    ) {
                        reminders.filter { it.usuarioId == selectedAdultId }
                    } else {
                        reminders
                    }

                    val medicines = remindersFiltrados.map { reminder ->

                        val iconRes = when (reminder.forma.lowercase(Locale.ROOT)) {
                            "Cápsula" -> R.drawable.ic_capsule
                            "Tableta" -> R.drawable.ic_tablet
                            "Solución" -> R.drawable.ic_solution
                            else -> R.drawable.ic_gutte
                        }

                        MedicineUi(
                            id = reminder.medId,
                            name = reminder.nombre,
                            timeText = reminder.proximoRecordatorioTexto,
                            quantity = reminder.cantidad,
                            iconRes = iconRes
                        )
                    }

                    if (medicines.isEmpty()) {
                        rvMedicines.visibility = View.GONE
                        tvEmptyMedicines.visibility = View.VISIBLE
                    } else {
                        rvMedicines.visibility = View.VISIBLE
                        tvEmptyMedicines.visibility = View.GONE
                        rvMedicines.adapter = MedicinesAdapter(
                            items = medicines,
                            onCheckedChange = { med, checked ->
                                Log.d("HomeActivity", "Medicamento ${med.name} tomado = $checked")
                            },
                            onEditClick = { med ->
                                openEditMedicine(med)
                            },
                            onDeleteClick = { med ->
                                AlertDialog.Builder(this@HomeActivity)
                                    .setTitle("Eliminar medicamento")
                                    .setMessage("¿Seguro que quieres eliminar \"${med.name}\"?")
                                    .setPositiveButton("Eliminar") { _, _ ->
                                        lifecycleScope.launch {
                                            val ok = deleteMedicine(medsBaseUrl, med.id)
                                            if (ok) {
                                                loadMedicines()
                                                Toast.makeText(
                                                    this@HomeActivity,
                                                    "Medicamento eliminado",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    this@HomeActivity,
                                                    "No se pudo eliminar el medicamento",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                    .setNegativeButton("Cancelar", null)
                                    .show()
                            }
                        )


                    }
                }
            },
            initialSelectedIndex = centerIndex
        )

        rvDays.adapter = adapter

        rvDays.post {
            rvDays.scrollToPosition(centerIndex)
        }

        if (userType.contains("FAMILIAR", ignoreCase = true) && userId.isNotBlank()) {
            lifecycleScope.launch {
                val adultos = obtenerAdultosVinculados(usersBaseUrl, userId)

                if (adultos.isNotEmpty()) {
                    // llenar spinner
                    val nombres = adultos.map { "${it.nombre} ${it.apellido}" }
                    val adapterSpinner = android.widget.ArrayAdapter(
                        this@HomeActivity,
                        android.R.layout.simple_spinner_item,
                        nombres
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                    spinnerAdultos.adapter = adapterSpinner
                    spinnerAdultos.visibility = View.VISIBLE

                    // seleccionar primero por defecto
                    selectedAdultId = adultos.first().id

                    spinnerAdultos.onItemSelectedListener =
                        object : android.widget.AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: android.widget.AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                selectedAdultId = adultos[position].id
                                // recargar medicamentos para ese adulto y fecha actual
                                loadMedicines()
                            }

                            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                                // nada
                            }
                        }
                } else {
                    spinnerAdultos.visibility = View.GONE
                    selectedAdultId = null
                }
            }
        }

        if (userId.isNotBlank()) {
            lifecycleScope.launch {
                val endpoint = if (userType.contains("FAMILIAR", ignoreCase = true)) {
                    "$medsBaseUrl/by-date-familiar"
                } else {
                    "$medsBaseUrl/by-date"
                }

                val reminders = obtenerRecordatoriosHome(endpoint, userId, selectedDateIso)

                // Si es FAMILIAR y hay adulto seleccionado, filtramos
                val remindersFiltrados = if (userType.contains("FAMILIAR", ignoreCase = true)
                    && selectedAdultId != null
                ) {
                    reminders.filter { it.usuarioId == selectedAdultId }
                } else {
                    reminders
                }

                val medicines = remindersFiltrados.map { reminder ->
                    val iconRes = when (reminder.forma.lowercase(Locale.ROOT)) {
                        "Cápsula" -> R.drawable.ic_capsule
                        "Tableta" -> R.drawable.ic_tablet
                        "Solución" -> R.drawable.ic_solution
                        else -> R.drawable.ic_gutte
                    }

                    MedicineUi(
                        id = reminder.medId,
                        name = reminder.nombre,
                        timeText = reminder.proximoRecordatorioTexto,
                        quantity = reminder.cantidad,
                        iconRes = iconRes
                    )
                }

                if (medicines.isEmpty()) {
                    rvMedicines.visibility = View.GONE
                    tvEmptyMedicines.visibility = View.VISIBLE
                } else {
                    rvMedicines.visibility = View.VISIBLE
                    tvEmptyMedicines.visibility = View.GONE

                    val medicinesAdapter = MedicinesAdapter(
                        items = medicines,
                        onCheckedChange = { med, checked ->
                            Log.d("HomeActivity", "Medicamento ${med.name} tomado = $checked")
                        },
                        onEditClick = { med ->
                            openEditMedicine(med)
                        },
                        onDeleteClick = { med ->
                            lifecycleScope.launch {
                                val ok = deleteMedicine(medsBaseUrl, med.id)
                                if (ok) {
                                    loadMedicines()
                                } else {
                                    Toast.makeText(
                                        this@HomeActivity,
                                        "No se pudo eliminar el medicamento",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    )

                    rvMedicines.adapter = medicinesAdapter
                }
            }
        } else {
            Log.d("HomeActivity", "No hay usuario en sesión, no se cargan medicamentos")
        }
    }

    override fun onResume() {
        super.onResume()
        loadMedicines()
    }

    private fun loadMedicines() {
        if (userId.isBlank()) return

        val endpoint = if (userType.contains("FAMILIAR", ignoreCase = true)) {
            "$medsBaseUrl/by-date-familiar"
        } else {
            "$medsBaseUrl/by-date"
        }

        lifecycleScope.launch {
            val reminders = obtenerRecordatoriosHome(endpoint, userId, selectedDateIso)

            val remindersFiltrados = if (userType.contains("FAMILIAR", ignoreCase = true)
                && selectedAdultId != null
            ) {
                reminders.filter { it.usuarioId == selectedAdultId }
            } else {
                reminders
            }

            val medicines = remindersFiltrados.map { reminder ->
                val iconRes = when (reminder.forma.lowercase(Locale.ROOT)) {
                    "Cápsula" -> R.drawable.ic_capsule
                    "Tableta" -> R.drawable.ic_tablet
                    "Solución" -> R.drawable.ic_solution
                    else -> R.drawable.ic_gutte
                }

                MedicineUi(
                    id = reminder.medId,
                    name = reminder.nombre,
                    timeText = reminder.proximoRecordatorioTexto,
                    quantity = reminder.cantidad,
                    iconRes = iconRes
                )
            }

            if (medicines.isEmpty()) {
                rvMedicines.visibility = View.GONE
                tvEmptyMedicines.visibility = View.VISIBLE
            } else {
                rvMedicines.visibility = View.VISIBLE
                tvEmptyMedicines.visibility = View.GONE

                val medicinesAdapter = MedicinesAdapter(
                    items = medicines,
                    onCheckedChange = { med, checked ->
                        Log.d("HomeActivity", "Medicamento ${med.name} tomado = $checked")
                    },
                    onEditClick = { med ->
                        openEditMedicine(med)
                    },
                    onDeleteClick = { med ->
                        lifecycleScope.launch {
                            val ok = deleteMedicine(medsBaseUrl, med.id)
                            if (ok) {
                                loadMedicines()
                            } else {
                                Toast.makeText(
                                    this@HomeActivity,
                                    "No se pudo eliminar el medicamento",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )


                rvMedicines.adapter = medicinesAdapter
            }
        }
    }

    fun AddMedicine(view: View) {
        val intent = Intent(this, AddMedicineActivity::class.java)
        startActivity(intent)
    }
    fun openMonitor(view: View) {
        val intent = Intent(this, VitalSignsActivity::class.java)
        startActivity(intent)
    }

    private fun openEditMedicine(medicine: MedicineUi) {
        val intent = Intent(this, AddMedicineActivity::class.java)
        intent.putExtra("extra_medicine", medicine)
        startActivity(intent)
    }
}

suspend fun obtenerRecordatoriosHome(
    endpointUrl: String,
    userId: String,
    selectedDate: String
): List<HomeReminder> = withContext(Dispatchers.IO) {

    val resultado = mutableListOf<HomeReminder>()

    try {
        val urlMeds = URL("$endpointUrl/$userId?date=$selectedDate")

        val connMeds = (urlMeds.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
        }

        val codeMeds = connMeds.responseCode
        Log.d("HomeDebug", "codeMeds = $codeMeds")

        if (codeMeds !in 200..299) {
            val err = connMeds.errorStream?.bufferedReader()?.use { it.readText() }
            Log.e("HomeDebug", "Error listando medicamentos: $err")
            connMeds.disconnect()
            return@withContext emptyList()
        }

        val medsText = connMeds.inputStream.bufferedReader().use { it.readText() }
        connMeds.disconnect()

        val medsArray = JSONArray(medsText.trim())

        val selectedLocalDate = java.time.LocalDate.parse(selectedDate)

        for (i in 0 until medsArray.length()) {
            val med = medsArray.getJSONObject(i)

            val medId       = med.optString("_id", "")
            val nombre      = med.optString("nombre", "Medicamento")
            val usuarioId   = med.optString("id_usuario", userId)
            val forma       = med.optString("forma", "")
            val cantidad    = med.optInt("cantidad", 0)

            if (medId.isBlank()) continue

            val fechaInicioStr = med.optString("fecha_inicio", "")
            val fechaFinStr    = med.optString("fecha_fin", "")


            val isWithinRange = try {
                val fechaInicio = java.time.LocalDate.parse(fechaInicioStr)
                val fechaFin    = java.time.LocalDate.parse(fechaFinStr)


                !selectedLocalDate.isBefore(fechaInicio) &&
                        !selectedLocalDate.isAfter(fechaFin)
            } catch (e: Exception) {

                true
            }


            if (!isWithinRange) continue


            val horasArray = med.optJSONArray("horas_recordatorio")
            val horaRaw = if (horasArray != null && horasArray.length() > 0) {
                horasArray.getString(0)
            } else {
                ""
            }

            val textoHora = if (horaRaw.isNotBlank()) {
                try {
                    val parser = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val date = parser.parse(horaRaw)
                    val fmt = SimpleDateFormat("h:mm a", Locale("es", "ES"))
                    val horaBonita = fmt.format(date!!).lowercase()


                    "A las $horaBonita"
                } catch (e: Exception) {
                    e.printStackTrace()
                    "Sin hora específica"
                }
            } else {
                "Sin hora específica"
            }

            resultado.add(
                HomeReminder(
                    medId = medId,
                    nombre = nombre,
                    proximoRecordatorioTexto = textoHora,
                    usuarioId = usuarioId,
                    forma = forma,
                    cantidad = cantidad
                )
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("HomeDebug", "Excepción en obtenerRecordatoriosHome: ${e.message}")
    }

    resultado
}

suspend fun deleteMedicine(
    medsBaseUrl: String,
    medId: String
): Boolean = withContext(Dispatchers.IO) {
    try {
        val url = URL("$medsBaseUrl/deleteMed")
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




suspend fun obtenerAdultosVinculados(
    usersBaseUrl: String,
    familiarId: String
): List<AdultoVinculado> = withContext(Dispatchers.IO) {
    try {
        val url = URL("$usersBaseUrl/$familiarId/adultos-mayores")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
        }

        val code = conn.responseCode
        if (code !in 200..299) {
            val err = conn.errorStream?.bufferedReader()?.use { it.readText() }
            Log.e("HomeDebug", "Error obteniendo adultos-mayores: $err")
            conn.disconnect()
            return@withContext emptyList()
        }

        val text = conn.inputStream.bufferedReader().use { it.readText() }
        conn.disconnect()

        val root = JSONObject(text)
        val arr = root.optJSONArray("adultosMayores") ?: return@withContext emptyList()

        val resultado = mutableListOf<AdultoVinculado>()
        for (i in 0 until arr.length()) {
            val item = arr.getJSONObject(i)
            resultado.add(
                AdultoVinculado(
                    id = item.optString("_id", ""),
                    nombre = item.optString("nombre", ""),
                    apellido = item.optString("apellido", "")
                )
            )
        }

        resultado.filter { it.id.isNotBlank() && it.nombre.isNotBlank() }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}