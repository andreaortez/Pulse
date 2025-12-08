package com.example.medilink.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MedicineUi(
    val id: String,
    val name: String,
    val timeText: String,
    val quantity: Int,
    var taken: Boolean = false,
    val iconRes: Int
) : Parcelable
