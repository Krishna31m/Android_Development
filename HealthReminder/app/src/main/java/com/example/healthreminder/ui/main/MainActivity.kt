package com.example.healthreminder.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.healthreminder.R
import com.example.healthreminder.ui.fragments.DashboardFragment
import com.example.healthreminder.ui.fragments.HealthStatsFragment
import com.example.healthreminder.ui.fragments.ProfileFragment
import com.example.healthreminder.ui.fragments.RemindersFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        bottomNav = findViewById(R.id.bottom_navigation)

        // Setup toolbar
        setSupportActionBar(toolbar)

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
        }

        // Bottom navigation listener
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(DashboardFragment())
                    toolbar.title = "Dashboard"
                    true
                }
                R.id.nav_reminders -> {
                    loadFragment(RemindersFragment())
                    toolbar.title = "Reminders"
                    true
                }
                R.id.nav_health_stats -> {
                    loadFragment(HealthStatsFragment())
                    toolbar.title = "Health Stats"
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    toolbar.title = "Profile"
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}