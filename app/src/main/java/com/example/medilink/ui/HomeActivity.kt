package com.example.medilink.ui

import DayCalendarAdapter
import DayUi
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medilink.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val rvDays = findViewById<RecyclerView>(R.id.rvDays)
        val tvToday = findViewById<TextView>(R.id.tvToday)

        val localeEs = Locale("es", "ES")
        val today = LocalDate.now()


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


        val rvMedicines = findViewById<RecyclerView>(R.id.rvMedicines)
        rvMedicines.layoutManager = LinearLayoutManager(this)

        val medicines = listOf(
            MedicineUi(
                name = "Antihistamínico",
                timeText = "Hoy a las 6:00 a. m.",
                extraText = "Tengo alarma a esa hora"
            ),
            MedicineUi(
                name = "Almagel, 200 ml",
                timeText = "Hoy después del almuerzo",
                extraText = "Tomar con el estómago lleno"
            ),
            MedicineUi(
                name = "Ibuprofeno 400 mg",
                timeText = "Hoy a las 9:00 p. m.",
                extraText = "No tomar con el estómago vacío"
            )
        )

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


    fun AddMedicine(view: View) {
        val intent = Intent(this, AddMedicineActivity::class.java)
        startActivity(intent)
    }


    private fun openEditMedicine(medicine: MedicineUi) {
        val intent = Intent(this, AddMedicineActivity::class.java)
        // IMPORTANTE: MedicineUi debe ser Parcelable o Serializable
        intent.putExtra("extra_medicine", medicine)
        startActivity(intent)
    }
}
