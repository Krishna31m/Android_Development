package com.example.events

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.events.adapter.ComedyAdapter
import com.example.events.model.Comedy

class ComedyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comedy)

        supportActionBar?.title = "Comedy Section"

        val comedyRecyclerView: RecyclerView = findViewById(R.id.comedyRecyclerView)

        val comedyList = listOf(
            Comedy("Stand-Up Night", "Laugh out loud with top comedians!", R.drawable.standup),
            Comedy("Open Mic", "Open mic for all budding comedians.", R.drawable.open_mic),
            Comedy("Comedy Roast", "A fiery roast show with celebs!", R.drawable.roast_show)
        )

        val adapter = ComedyAdapter(this, comedyList)
        comedyRecyclerView.layoutManager = LinearLayoutManager(this)
        comedyRecyclerView.adapter = adapter
    }
}
