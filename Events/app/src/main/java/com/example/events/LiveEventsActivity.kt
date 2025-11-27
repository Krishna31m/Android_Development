package com.example.events

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LiveEventsActivity : AppCompatActivity() {

    private lateinit var liveEventsRecyclerView: RecyclerView
    private lateinit var liveEventsAdapter: LiveEventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_events)

        liveEventsRecyclerView = findViewById(R.id.liveEventsRecyclerView)

        val liveEventsList = listOf(
            LiveEvent(R.drawable.concert, "Music Concert - DJ Night", "Enjoy an electrifying night with top DJs."),
            LiveEvent(R.drawable.comedy_show, "Stand-up Comedy Show", "Laugh out loud with famous comedians."),
            LiveEvent(R.drawable.rock_band, "Rock Band Live", "Experience the energy of a live rock band."),
            LiveEvent(R.drawable.food_festival, "Food Festival Live", "Taste dishes from around the world."),
            LiveEvent(R.drawable.football_final, "Football Final Live Stream", "Catch the exciting football final live.")
        )

        liveEventsAdapter = LiveEventsAdapter(liveEventsList)
        liveEventsRecyclerView.layoutManager = LinearLayoutManager(this)
        liveEventsRecyclerView.adapter = liveEventsAdapter
    }
}
