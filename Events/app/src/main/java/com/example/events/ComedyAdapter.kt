package com.example.events.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.events.R
import com.example.events.model.Comedy

class ComedyAdapter(private val context: Context, private val comedyList: List<Comedy>) :
    RecyclerView.Adapter<ComedyAdapter.ComedyViewHolder>() {

    class ComedyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val comedyTitle: TextView = itemView.findViewById(R.id.comedyTitle)
        val comedyDescription: TextView = itemView.findViewById(R.id.comedyDescription)
        val comedyImage: ImageView = itemView.findViewById(R.id.comedyImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComedyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_comedy, parent, false)
        return ComedyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComedyViewHolder, position: Int) {
        val comedy = comedyList[position]
        holder.comedyTitle.text = comedy.title
        holder.comedyDescription.text = comedy.description
        holder.comedyImage.setImageResource(comedy.imageResId)
    }

    override fun getItemCount(): Int = comedyList.size
}
