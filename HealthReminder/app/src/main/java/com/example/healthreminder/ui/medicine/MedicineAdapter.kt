package com.example.healthreminder.ui.medicine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthreminder.R
import com.example.healthreminder.data.model.Medicine
import com.google.android.material.card.MaterialCardView

class MedicineAdapter(
    private val medicines: List<Medicine>,
    private val onItemClick: (Medicine) -> Unit
) : RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    inner class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.card_medicine_item)
        val tvName: TextView = itemView.findViewById(R.id.tv_medicine_name)
        val tvDosage: TextView = itemView.findViewById(R.id.tv_dosage)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val tvFrequency: TextView = itemView.findViewById(R.id.tv_frequency)

        fun bind(medicine: Medicine) {
            tvName.text = medicine.name
            tvDosage.text = medicine.dosage
            tvTime.text = medicine.time
            tvFrequency.text = medicine.frequency

            cardView.setOnClickListener {
                onItemClick(medicine)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine, parent, false)
        return MedicineViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        holder.bind(medicines[position])
    }

    override fun getItemCount() = medicines.size
}