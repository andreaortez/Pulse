package com.example.medilink.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize

data class MedicineUi(
    val id: String,
    val name: String,
    val quantity: Int,
    val iconRes: Int,
    val fechaInicio: String,
    val fechaFin: String,
    val forma: String,
    val horas: List<String>,
    var taken: Boolean = false,

) : Parcelable {

    val timeText: String
        get() = if (horas.isNotEmpty()) horas.joinToString(", ") else "Sin hora"
}
