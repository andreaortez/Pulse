package com.example.medilink.ui

import DayCalendarAdapter
import DayUi
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medilink.R // Important: Import R from your app's package
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

        rvDays.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val today = LocalDate.now()

        val days = listOf(
            DayUi(today.minusDays(2)),
            DayUi(today.minusDays(1)),
            DayUi(today),
            DayUi(today.plusDays(1)),
            DayUi(today.plusDays(2))
        )

        val centerIndex = 2

        val adapter = DayCalendarAdapter(days = days, onDaySelected = { selectedDate ->
                Locale("es", "ES")
                tvToday.text = "Hoy, " +
                        selectedDate.format(DateTimeFormatter.ofPattern("dd MMM"))
            }, initialSelectedIndex = centerIndex,)

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

        val medicinesAdapter = MedicinesAdapter(medicines) { med, checked ->

            Log.d("HomeActivity", "Medicamento ${med.name} tomado = $checked")
        }

        rvMedicines.adapter = medicinesAdapter

    }
    }

