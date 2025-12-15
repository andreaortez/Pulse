package com.example.medilink.ui

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.medilink.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale


class MedicinesAdapter(
    private val items: List<MedicineUi>,
    private val selectedDateIso: String,
    private val onCheckedChange: (MedicineUi, Boolean) -> Unit,
    private val onEditClick: (MedicineUi) -> Unit,
    private val onDeleteClick: (MedicineUi) -> Unit
) : RecyclerView.Adapter<MedicinesAdapter.MedicineViewHolder>() {

    inner class MedicineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)

        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteMed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine, parent, false)
        return MedicineViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val item = items[position]

        holder.ivIcon.setImageResource(item.iconRes)
        holder.tvTitle.text = item.name
        val hourToShow = getNextReminderHourAllApis(item.horas, selectedDateIso)
        holder.tvTime.text = if (hourToShow == "Sin hora") hourToShow else "A las $hourToShow"

        holder.tvQuantity.text = "Cantidad: ${item.quantity}"




        holder.btnEdit.setOnClickListener {
            onEditClick(item)
        }
        holder.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
        holder.ivIcon.setImageResource(R.drawable.ic_pill)
    }

    override fun getItemCount(): Int = items.size
}
@RequiresApi(Build.VERSION_CODES.O)
private fun getNextReminderHourAllApis(horas: List<String>, selectedDateIso: String): String {
    if (horas.isEmpty()) return "Sin hora"


    val cleaned = horas
        .map { it.trim() }
        .filter { it.matches(Regex("""^\d{1,2}:\d{2}$""")) }

    if (cleaned.isEmpty()) return "Sin hora"

    val isoParser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val timeParser = SimpleDateFormat("H:mm", Locale.US)
    val timeOut = SimpleDateFormat("HH:mm", Locale.US)

    val selectedCal = Calendar.getInstance()
    selectedCal.time = isoParser.parse(selectedDateIso) ?: return cleaned.first()

    val todayCal = Calendar.getInstance()
    val isSameDay =
        selectedCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                selectedCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)

    val timesMinutes = cleaned.mapNotNull { h ->
        try {
            val d = timeParser.parse(h) ?: return@mapNotNull null
            val cal = Calendar.getInstance().apply { time = d }
            cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        } catch (_: Exception) {
            null
        }
    }.sorted()

    if (timesMinutes.isEmpty()) return "Sin hora"


    if (!isSameDay) {
        val first = timesMinutes.first()
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, first / 60)
            set(Calendar.MINUTE, first % 60)
        }
        return timeOut.format(cal.time)
    }


    val now = Calendar.getInstance()
    val nowMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

    val next = timesMinutes.firstOrNull { it >= nowMinutes } ?: timesMinutes.first()

    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, next / 60)
        set(Calendar.MINUTE, next % 60)
    }
    return timeOut.format(cal.time)
}
