package com.example.events



import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(private val context: Context, private val eventList: List<Event>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.eventImage.setImageResource(event.imageResId)
        holder.eventTitle.text = event.title
        holder.eventDescription.text = event.description

        holder.viewDetailsButton.setOnClickListener {
            val intent = Intent(context, EventDetailActivity::class.java).apply {
                putExtra("imageResId", event.imageResId)
                putExtra("title", event.title)
                putExtra("description", event.description)
                putExtra("artist", event.artist)
                putExtra("dateTime", event.dateTime)
                putExtra("venue", event.venue)
                putExtra("price", event.price)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = eventList.size

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventImage: ImageView = view.findViewById(R.id.eventImage)
        val eventTitle: TextView = view.findViewById(R.id.eventTitle)
        val eventDescription: TextView = view.findViewById(R.id.eventDescription)
        val viewDetailsButton: Button = view.findViewById(R.id.viewDetailsButton)
    }
}
