package com.example.healthreminder.ui.diet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthreminder.R
import com.example.healthreminder.data.model.Meal
import com.google.android.material.card.MaterialCardView

class MealAdapter(
    private val meals: List<Meal>,
    private val onItemClick: (Meal) -> Unit
) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    inner class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.card_meal_item)
        val tvMealType: TextView = itemView.findViewById(R.id.tv_meal_type)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val tvCalories: TextView = itemView.findViewById(R.id.tv_calories)

        fun bind(meal: Meal) {
            tvMealType.text = meal.mealType
            tvDescription.text = if (meal.description.isEmpty()) "No description" else meal.description
            tvTime.text = meal.time
            tvCalories.text = if (meal.calories > 0) "${meal.calories} cal" else "No calories"

            cardView.setOnClickListener {
                onItemClick(meal)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meal, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(meals[position])
    }

    override fun getItemCount() = meals.size
}