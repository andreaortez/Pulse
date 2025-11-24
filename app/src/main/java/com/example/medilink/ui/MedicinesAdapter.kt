package com.example.medilink.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView

import com.example.medilink.R
import androidx.recyclerview.widget.RecyclerView


class MedicinesAdapter(
    private val items: List<MedicineUi>,
    private val onCheckedChange: (MedicineUi, Boolean) -> Unit
) : RecyclerView.Adapter<MedicinesAdapter.MedicineViewHolder>() {

    inner class MedicineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvExtra: TextView = view.findViewById(R.id.tvExtra)
        val cbTaken: CheckBox = view.findViewById(R.id.cbTaken)
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine, parent, false)
        return MedicineViewHolder(view)
    }

    // 2. The duplicate onCreateViewHolder function has been removed.

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val item = items[position]

        holder.tvName.text = item.name
        holder.tvTime.text = item.timeText
        holder.tvExtra.text = item.extraText


        holder.cbTaken.setOnCheckedChangeListener(null)
        holder.cbTaken.isChecked = item.taken

        holder.cbTaken.setOnCheckedChangeListener { _, isChecked ->

            item.taken = isChecked
            onCheckedChange(item, isChecked)
        }
    }

    override fun getItemCount(): Int = items.size
}
