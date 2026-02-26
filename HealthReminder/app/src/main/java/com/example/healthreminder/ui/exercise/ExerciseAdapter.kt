package com.example.healthreminder.ui.exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthreminder.R
import com.example.healthreminder.data.model.Exercise
import com.google.android.material.card.MaterialCardView

class ExerciseAdapter(
    private val exercises: List<Exercise>,
    private val onItemClick: (Exercise) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.card_exercise_item)
        val tvName: TextView = itemView.findViewById(R.id.tv_exercise_name)
        val tvType: TextView = itemView.findViewById(R.id.tv_exercise_type)
        val tvDuration: TextView = itemView.findViewById(R.id.tv_duration)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val tvDays: TextView = itemView.findViewById(R.id.tv_days)

        fun bind(exercise: Exercise) {
            tvName.text = exercise.name
            tvType.text = exercise.type
            tvDuration.text = "${exercise.duration} min"
            tvTime.text = exercise.time
            tvDays.text = exercise.days.joinToString(", ")

            cardView.setOnClickListener {
                onItemClick(exercise)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    override fun getItemCount() = exercises.size
}