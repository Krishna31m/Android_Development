package com.krishna.varunaapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.krishna.varunaapp.R
import com.krishna.varunaapp.databinding.ItemAlertBinding
import com.krishna.varunaapp.models.Alert
import java.text.SimpleDateFormat
import java.util.*

class AlertAdapter(
    private val alerts: List<Alert>
) : RecyclerView.Adapter<AlertAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAlertBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlertBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alert = alerts[position]

        holder.binding.tvAlertTitle.text = alert.title
        holder.binding.tvAlertMessage.text = alert.message
        holder.binding.tvAlertTime.text = formatTime(alert.createdAt)

        // Set color based on alert type
        val iconColor = when (alert.type) {
            "urgent" -> R.color.alert_urgent // Red
            "warning" -> R.color.alert_warning // Orange
            "success" -> R.color.alert_success // Green
            else -> R.color.alert_info // Blue
        }

        holder.binding.viewAlertIndicator.setBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, iconColor)
        )
    }

    override fun getItemCount() = alerts.size

    private fun formatTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000} minutes ago"
            diff < 86400000 -> "${diff / 3600000} hours ago"
            diff < 604800000 -> "${diff / 86400000} days ago"
            else -> {
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                sdf.format(Date(timestamp))
            }
        }
    }
}