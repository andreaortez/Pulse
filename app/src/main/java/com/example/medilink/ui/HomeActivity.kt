package com.example.medilink.ui

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
import com.example.medilink.BuildConfig
import com.example.medilink.R
import com.example.medilink.SessionManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class HomeActivity : AppCompatActivity() {
    private lateinit var rvMedicines: RecyclerView
    private lateinit var medsBaseUrl: String

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val rvDays = findViewById<RecyclerView>(R.id.rvDays)
        val tvToday = findViewById<TextView>(R.id.tvToday)

        rvMedicines = findViewById<RecyclerView>(R.id.rvMedicines)
        rvMedicines.layoutManager = LinearLayoutManager(this)

        val localeEs = Locale("es", "ES")
        val today = LocalDate.now()

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
            },
            initialSelectedIndex = centerIndex
        )

        rvDays.adapter = adapter

        rvDays.post {
            rvDays.scrollToPosition(centerIndex)
        }

        medsBaseUrl = BuildConfig.MEDS_URL
        val userId = SessionManager.getUserId(this) ?: ""

        Log.d("HomeDebug", "medsBaseUrl = $medsBaseUrl")
        Log.d("HomeDebug", "userId = '$userId'")

        if (userId.isNotBlank()) {
            lifecycleScope.launch {
                val reminders = obtenerRecordatoriosHome(medsBaseUrl, userId)

                val medicines = reminders.map { reminder ->
                    MedicineUi(
                        name = reminder.nombre,
                        timeText = reminder.proximoRecordatorioTexto,
                        extraText = ""
                    )
                }

                val medicinesAdapter = MedicinesAdapter(
                    items = medicines,
                    onCheckedChange = { med, checked ->
                        Log.d("HomeActivity", "Medicamento ${med.name} tomado = $checked")
                    },
                    onEditClick = { med ->
                        openEditMedicine(med)
                    }
                )

                rvMedicines.adapter = medicinesAdapter
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
        val userId = SessionManager.getUserId(this) ?: ""

        Log.d("HomeDebug", "loadMedicines -> userId = '$userId', medsBaseUrl = $medsBaseUrl")

        if (userId.isNotBlank()) {
            lifecycleScope.launch {
                val reminders = obtenerRecordatoriosHome(medsBaseUrl, userId)
                Log.d("HomeDebug", "Reminders recibidos en loadMedicines: ${reminders.size}")

                val medicines = reminders.map { reminder ->
                    MedicineUi(
                        name = reminder.nombre,
                        timeText = reminder.proximoRecordatorioTexto,
                        extraText = ""
                    )
                }

                val medicinesAdapter = MedicinesAdapter(
                    items = medicines,
                    onCheckedChange = { med, checked ->
                        Log.d("HomeActivity", "Medicamento ${med.name} tomado = $checked")
                    },
                    onEditClick = { med ->
                        openEditMedicine(med)
                    }
                )

                rvMedicines.adapter = medicinesAdapter
            }
        } else {
            Log.d("HomeDebug", "No hay userId en sesión, no se cargan medicamentos")
        }
    }


    fun AddMedicine(view: View) {
        val intent = Intent(this, AddMedicineActivity::class.java)
        startActivity(intent)
    }

    private fun openEditMedicine(medicine: MedicineUi) {
        val intent = Intent(this, AddMedicineActivity::class.java)
        intent.putExtra("extra_medicine", medicine)
        startActivity(intent)
    }
}


data class HomeReminder(
    val medId: String,
    val nombre: String,
    val proximoRecordatorioTexto: String
)

fun formatearProximoRecordatorio(iso: String): String {
    val posiblesFormatos = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss"
    )

    var date: java.util.Date? = null
    for (f in posiblesFormatos) {
        try {
            val parser = SimpleDateFormat(f, Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            date = parser.parse(iso)
            if (date != null) break
        } catch (_: Exception) { }
    }

    if (date == null) return ""

    val hoy = Calendar.getInstance()
    val otro = Calendar.getInstance().apply { time = date }

    val mismoDia = hoy.get(Calendar.YEAR) == otro.get(Calendar.YEAR) &&
            hoy.get(Calendar.DAY_OF_YEAR) == otro.get(Calendar.DAY_OF_YEAR)

    val horaFormat = SimpleDateFormat("h:mm a", Locale("es", "ES"))
    val horaTexto = horaFormat.format(date).lowercase()

    return if (mismoDia) {
        "Hoy a las $horaTexto"
    } else {
        val fechaFormat = SimpleDateFormat("d MMM", Locale("es", "ES"))
        "El ${fechaFormat.format(date)} a las $horaTexto"
    }
}

suspend fun obtenerRecordatoriosHome(
    medsBaseUrl: String,
    userId: String
): List<HomeReminder> = withContext(Dispatchers.IO) {
    val resultado = mutableListOf<HomeReminder>()

    try {
        val urlMeds = URL("$medsBaseUrl/listarMedicamentosActivos/$userId")
        val connMeds = (urlMeds.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
        }

        val codeMeds = connMeds.responseCode
        Log.d("HomeDebug", "codeMeds = $codeMeds")

        if (codeMeds !in 200..299) {
            val err = connMeds.errorStream?.bufferedReader()?.use { it.readText() }
            Log.e("HomeDebug", "Error listando medicamentos activos: $err")
            connMeds.disconnect()
            return@withContext emptyList()
        }

        val medsText = connMeds.inputStream.bufferedReader().use { it.readText() }
        connMeds.disconnect()
        Log.d("HomeDebug", "Respuesta meds: $medsText")

        val obj = JSONObject(medsText)
        val medsArray = obj.getJSONArray("medicamentos")

        for (i in 0 until medsArray.length()) {
            val med = medsArray.getJSONObject(i)
            val medId = med.optString("_id", "")
            val nombre = med.optString("nombre", "Medicamento")

            if (medId.isBlank()) continue

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
                    "Hoy a las $horaBonita"
                } catch (e: Exception) {
                    e.printStackTrace()
                    "Hoy"
                }
            } else {
                "Hoy"
            }

            resultado.add(
                HomeReminder(
                    medId = medId,
                    nombre = nombre,
                    proximoRecordatorioTexto = textoHora
                )
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    Log.d("HomeDebug", "Reminders construidos: ${resultado.size}")
    resultado
}
