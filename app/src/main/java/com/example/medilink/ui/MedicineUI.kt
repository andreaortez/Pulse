package com.example.medilink.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MedicineUi(
    val name: String,
    val timeText: String,
    val extraText: String,
    var taken: Boolean = false
) : Parcelable
