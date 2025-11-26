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


class MedicinesAdapter(
    private val items: List<MedicineUi>,
    private val onCheckedChange: (MedicineUi, Boolean) -> Unit,
    private val onEditClick: (MedicineUi) -> Unit
) : RecyclerView.Adapter<MedicinesAdapter.MedicineViewHolder>() {

    inner class MedicineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvTitle)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvExtra: TextView = view.findViewById(R.id.tvTag)
        val cbTaken: CheckBox = view.findViewById(R.id.checkTaken)
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)

        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)


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

            item.taken= isChecked
            onCheckedChange(item, isChecked)
        }
        holder.tvTitle.text = item.name

        holder.btnEdit.setOnClickListener {
            onEditClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
