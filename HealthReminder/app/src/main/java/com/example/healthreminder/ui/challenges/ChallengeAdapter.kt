package com.example.healthreminder.ui.challenges

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthreminder.R
import com.example.healthreminder.data.model.HealthChallenge
import com.google.android.material.card.MaterialCardView

class ChallengeAdapter(
    private val challenges: List<HealthChallenge>,
    private val onItemClick: (HealthChallenge) -> Unit
) : RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder>() {

    inner class ChallengeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.card_challenge_item)
        val tvChallengeName: TextView = itemView.findViewById(R.id.tv_challenge_name)
        val tvChallengeType: TextView = itemView.findViewById(R.id.tv_challenge_type)
        val tvStreak: TextView = itemView.findViewById(R.id.tv_streak)
        val tvProgress: TextView = itemView.findViewById(R.id.tv_progress)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_challenge)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)

        fun bind(challenge: HealthChallenge) {
            tvChallengeName.text = challenge.name
            tvChallengeType.text = challenge.type
            tvStreak.text = "🔥 ${challenge.currentStreak} day streak"

            val progress = (challenge.completedDays.size.toFloat() / challenge.duration * 100).toInt()
            tvProgress.text = "${challenge.completedDays.size}/${challenge.duration} days"
            progressBar.progress = progress

            tvStatus.text = if (challenge.isActive) "Active" else "Completed"
            tvStatus.setTextColor(
                if (challenge.isActive)
                    itemView.context.getColor(R.color.success)
                else
                    itemView.context.getColor(R.color.textSecondary)
            )

            cardView.setOnClickListener {
                onItemClick(challenge)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_challenge, parent, false)
        return ChallengeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        holder.bind(challenges[position])
    }

    override fun getItemCount() = challenges.size
}