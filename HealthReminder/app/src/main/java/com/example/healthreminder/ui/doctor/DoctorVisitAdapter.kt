package com.example.healthreminder.ui.doctor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthreminder.R
import com.example.healthreminder.data.model.DoctorVisit
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*

class DoctorVisitAdapter(
    private val visits: List<DoctorVisit>,
    private val onItemClick: (DoctorVisit) -> Unit
) : RecyclerView.Adapter<DoctorVisitAdapter.DoctorVisitViewHolder>() {

    inner class DoctorVisitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.card_doctor_visit_item)
        val tvDoctorName: TextView = itemView.findViewById(R.id.tv_doctor_name)
        val tvSpecialty: TextView = itemView.findViewById(R.id.tv_specialty)
        val tvDateTime: TextView = itemView.findViewById(R.id.tv_date_time)
        val tvLocation: TextView = itemView.findViewById(R.id.tv_location)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)

        fun bind(visit: DoctorVisit) {
            tvDoctorName.text = "Dr. ${visit.doctorName}"
            tvSpecialty.text = visit.specialty

            visit.date?.let {
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                tvDateTime.text = "${dateFormat.format(it.toDate())} at ${visit.time}"
            }

            tvLocation.text = visit.location
            tvStatus.text = if (visit.completed) "Completed" else "Upcoming"
            tvStatus.setTextColor(
                if (visit.completed)
                    itemView.context.getColor(R.color.success)
                else
                    itemView.context.getColor(R.color.warning)
            )

            cardView.setOnClickListener {
                onItemClick(visit)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorVisitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor_visit, parent, false)
        return DoctorVisitViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorVisitViewHolder, position: Int) {
        holder.bind(visits[position])
    }

    override fun getItemCount() = visits.size
}