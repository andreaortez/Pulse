package com.example.medilink.ui

data class MedicineUi(
    val name: String,
    val timeText: String,
    val extraText: String = "",
    var taken: Boolean = false
)
