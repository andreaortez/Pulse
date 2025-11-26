package com.example.medilink.ui


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class AddMedicineActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val med : MedicineUi? = intent.getParcelableExtra("extra_medicine")

        setContent {
            MaterialTheme {
                AddMedicineScreen(
                    onBackClick = { finish() },
                    onDoneClick = {
                        // aquí puedes guardar la info si luego usas ViewModel/DB
                        finish()
                    },
                    existingMedicine = med
                )
            }
        }
    }
}
