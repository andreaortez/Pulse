package com.example.medilink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.medilink.ui.AddMedicineScreen
import com.example.medilink.ui.MedicineUi

class AddMedicineActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val med : MedicineUi? = intent.getParcelableExtra("extra_medicine")
        val id = SessionManager.getUserId(this)

        if (id == null) {
            finish()
            return
        }

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