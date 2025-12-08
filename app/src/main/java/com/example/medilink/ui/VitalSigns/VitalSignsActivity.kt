package com.example.medilink.ui.VitalSigns

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.medilink.SessionManager
import com.example.medilink.ui.ChatBotActivity
import com.example.medilink.ui.MedicineUi
import com.example.medilink.ui.VitalSignsScreen

class VitalSignsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = SessionManager.getUserId(this)
        print("entered")
        if (id == null) {
            finish()
            return
        }
        print("entered")
        setContent {
            MaterialTheme {
                VitalSignsScreen(
                    onBackClick = { finish() },
                    onChatbotClick = {bpm, pressure, temperature ->
                        //cargar datos al chatbot
                        val intent = Intent(
                            this@VitalSignsActivity,
                            ChatBotActivity::class.java
                        ).apply {
                            putExtra(ChatBotActivity.BPM, bpm)
                            putExtra(ChatBotActivity.PRESSURE, pressure)
                            putExtra(ChatBotActivity.TEMPERATURE, temperature)
                        }
                        // 2. enviar datos
                        startActivity(intent)
                    },
                    idUsuario = id
                )
            }
        }
    }
}