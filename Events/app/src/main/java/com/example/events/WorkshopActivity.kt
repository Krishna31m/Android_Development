package com.example.events

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class WorkshopActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comedy)

        supportActionBar?.title = "Comedy Section"
    }
}