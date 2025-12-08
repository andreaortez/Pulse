package com.example.medilink.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medilink.R
import 

class MedicinesAdapter(
    private val items: List<MedicineUi>,
    private val onCheckedChange: (MedicineUi, Boolean) -> Unit,
    private val onEditClick: (MedicineUi) -> Unit,
    private val onDeleteClick: (MedicineUi) -> Unit
) : RecyclerView.Adapter<MedicinesAdapter.MedicineViewHolder>() {

    inner class MedicineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        val cbTaken: CheckBox = view.findViewById(R.id.checkTaken)
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
        holder.tvTime.text = item.timeText

        // Cantidad formateada a texto (ej: "Cantidad: 2")
        holder.tvQuantity.text = "Cantidad: ${item.quantity}"

        // Evitar que el listener se dispare al setear isChecked
        holder.cbTaken.setOnCheckedChangeListener(null)
        holder.cbTaken.isChecked = item.taken

        holder.cbTaken.setOnCheckedChangeListener { _, isChecked ->
            item.taken = isChecked
            onCheckedChange(item, isChecked)
        }

        holder.btnEdit.setOnClickListener {
            onEditClick(item)
        }
        holder.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}