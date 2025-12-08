package com.example.medilink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.medilink.ui.AddMedicineScreen
import com.example.medilink.ui.MedicineUi

class AddMedicineActivity : ComponentActivity() {
    private var editingMedicine: MedicineUi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val med : MedicineUi? = intent.getParcelableExtra("extra_medicine")
        val id = SessionManager.getUserId(this)
        val type = SessionManager.getUserType(this)
        editingMedicine = intent.getParcelableExtra("extra_medicine")

        if (id == null) {
            finish()
            return
        }

        if (type == null) {
            finish()
            return
        }

        setContent {
            MaterialTheme {
                AddMedicineScreen(
                    idUsuario = id.toString(),
                    type = type,
                    existingMedicine = editingMedicine,
                    oldMedId = editingMedicine?.id,
                    onBackClick = { finish() },
                    onDoneClick = { finish() }

                )
            }
        }
    }
}