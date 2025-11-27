package com.example.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LiveEventsAdapter(private val liveEvents: List<LiveEvent>) : RecyclerView.Adapter<LiveEventsAdapter.LiveEventViewHolder>() {

    class LiveEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventImage: ImageView = itemView.findViewById(R.id.eventImage)
        val eventTitle: TextView = itemView.findViewById(R.id.eventTitle)
        val eventDescription: TextView = itemView.findViewById(R.id.eventDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveEventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_live_event, parent, false)
        return LiveEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: LiveEventViewHolder, position: Int) {
        val event = liveEvents[position]
        holder.eventImage.setImageResource(event.imageResId)
        holder.eventTitle.text = event.title
        holder.eventDescription.text = event.description
    }

    override fun getItemCount(): Int = liveEvents.size
}
