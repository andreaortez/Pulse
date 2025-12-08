package com.example.medilink.ui.alerts

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.medilink.BuildConfig
import com.example.medilink.ui.components.Alert
import com.example.medilink.ui.components.AlertCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class AlertActivity : ComponentActivity() {
    private val alertsBaseUrl: String = BuildConfig.ALERTS_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("AlertDebug", "AlertActivity onCreate")

        val alertId = intent.getStringExtra("alert_id") ?: ""
        val mensaje = intent.getStringExtra("alert_message") ?: "Es hora de tu medicamento"
        val estado = intent.getStringExtra("alert_state") ?: "PENDIENTE"

        Log.d("AlertDebug", "AlertActivity datos: id=$alertId, msg=$mensaje")

        val alert = Alert(
            id = alertId,
            mensaje = mensaje,
            estado = estado
        )

        setContent {
            val scope = CoroutineScope(Dispatchers.Main)

            AlertCard(
                alert = alert,
                onMarkAsNotified = { id ->
                    scope.launch {
                        marcarAlerta("notified", id)
                        finish()
                    }
                },
                onMarkAsResolved = { id ->
                    scope.launch {
                        marcarAlerta("resolved", id)
                        finish()
                    }
                },
                onDismissRequest = {
                    Log.d("AlertDebug", "onDismissRequest")
                    finish()
                }
            )
        }
    }

    private suspend fun marcarAlerta(path: String, idAlert: String) {
        if (idAlert.isBlank()) {
            Log.e("AlertDebug", "idAlert vacío, no se llama al backend")
            return
        }

        withContext(Dispatchers.IO) {
            try {
                val url = URL("$alertsBaseUrl/$path/$idAlert")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    connectTimeout = 10_000
                    readTimeout = 10_000
                    doOutput = false
                }

                val code = conn.responseCode
                val body = (if (code in 200..299) conn.inputStream else conn.errorStream)
                    ?.bufferedReader()
                    ?.use { it.readText() }

                Log.d(
                    "AlertDebug",
                    "PUT $path/$idAlert -> code=$code, body=$body"
                )

                conn.disconnect()
            } catch (e: Exception) {
                Log.e("AlertDebug", "Error en marcarAlerta($path): ${e.message}", e)
            }
        }
    }
}
