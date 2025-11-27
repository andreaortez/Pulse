package com.example.medilink.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.medilink.SessionManager

class AddMedicineActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val med : MedicineUi? = intent.getParcelableExtra("extra_medicine")
        val id = SessionManager.getUserId(this)

        setContent {
            MaterialTheme {
                AddMedicineScreen(
                    onBackClick = { finish() },
                    onDoneClick = {
                        finish()
                    },
                    existingMedicine = med,
                    idUsuario = id.toString()
                )
            }
        }
    }
}
