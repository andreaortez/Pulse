package com.example.medilink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.medilink.ui.VincularFamiliarScreen

class VincularFamiliarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = SessionManager.getUserId(this)

        if (id == null) {
            finish()
            return
        }

        setContent {
            MaterialTheme {
                VincularFamiliarScreen(
                    idUsuarioActual = id,
                    onBackClick = { finish() }
                )
            }
        }
    }
}
