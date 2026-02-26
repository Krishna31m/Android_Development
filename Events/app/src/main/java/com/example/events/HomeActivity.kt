package com.example.events

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var eventRecyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Toolbar setup (your existing options-menu)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.homeToolbar)
        setSupportActionBar(toolbar)

        // RecyclerView setup
        val events = EventRepository.eventList
        eventRecyclerView = findViewById(R.id.eventRecyclerView)
        eventAdapter = EventAdapter(this, events)
        eventRecyclerView.layoutManager = LinearLayoutManager(this)
        eventRecyclerView.adapter = eventAdapter

        // BottomNavigationView setup
        bottomNav = findViewById(R.id.bottomNavigation2)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_movies -> {
                    startActivity(Intent(this, MoviesActivity::class.java))
                    true
                }
                R.id.menu_live -> {
                    startActivity(Intent(this, LiveEventsActivity::class.java))
                    true
                }
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // Your existing toolbar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_comedy -> startActivity(Intent(this, ComedyActivity::class.java))
            R.id.menu_music -> startActivity(Intent(this, MusicActivity::class.java))
            R.id.menu_plays -> startActivity(Intent(this, PlaysActivity::class.java))
            R.id.menu_sports -> startActivity(Intent(this, SportsActivity::class.java))
            R.id.menu_adventure -> startActivity(Intent(this, AdventureActivity::class.java))
            R.id.menu_workshop -> startActivity(Intent(this, WorkshopActivity::class.java))
            R.id.menu_kids_zone -> startActivity(Intent(this, KidsZoneActivity::class.java))
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}
