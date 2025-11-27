package com.example.events

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.events.adapter.MovieAdapter
import com.example.events.model.Movie

class MoviesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)

        supportActionBar?.title = "Movies Section"

        val movieRecyclerView: RecyclerView = findViewById(R.id.movieRecyclerView)

        val movies = listOf(
            Movie("Interstellar", "A team of explorers travel through a wormhole in space.", R.drawable.interstellar),
            Movie("Inception", "A thief steals corporate secrets through dream-sharing technology.", R.drawable.inception),
                Movie("The Dark Knight", "Batman faces the Joker in a battle for Gotham.", R.drawable.dark_knight)
        )

        val adapter = MovieAdapter(this, movies)
        movieRecyclerView.layoutManager = LinearLayoutManager(this)
        movieRecyclerView.adapter = adapter
    }
}
