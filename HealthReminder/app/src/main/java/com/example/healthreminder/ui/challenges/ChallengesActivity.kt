package com.example.healthreminder.ui.challenges

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthreminder.R
import com.example.healthreminder.data.model.HealthChallenge
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChallengesActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvChallenges: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var challengeAdapter: ChallengeAdapter
    private val challengeList = mutableListOf<HealthChallenge>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenges)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        rvChallenges = findViewById(R.id.rv_challenges)
        emptyState = findViewById(R.id.empty_state)
        fabAdd = findViewById(R.id.fab_add_challenge)

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Setup RecyclerView
        challengeAdapter = ChallengeAdapter(challengeList) { challenge ->
            editChallenge(challenge)
        }
        rvChallenges.layoutManager = LinearLayoutManager(this)
        rvChallenges.adapter = challengeAdapter

        // FAB click listener
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddChallengeActivity::class.java))
        }

        // Load challenges
        loadChallenges()
    }

    override fun onResume() {
        super.onResume()
        loadChallenges()
    }

    private fun loadChallenges() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("challenges")
            .get()
            .addOnSuccessListener { documents ->
                challengeList.clear()
                for (doc in documents) {
                    val challenge = doc.toObject(HealthChallenge::class.java)
                    challengeList.add(challenge)
                }

                // Sort: active first, then by start date
                challengeList.sortWith(compareByDescending<HealthChallenge> { it.isActive }
                    .thenByDescending { it.startDate })

                challengeAdapter.notifyDataSetChanged()

                // Show/hide empty state
                if (challengeList.isEmpty()) {
                    emptyState.visibility = View.VISIBLE
                    rvChallenges.visibility = View.GONE
                } else {
                    emptyState.visibility = View.GONE
                    rvChallenges.visibility = View.VISIBLE
                }
            }
    }

    private fun editChallenge(challenge: HealthChallenge) {
        val intent = Intent(this, AddChallengeActivity::class.java)
        intent.putExtra("CHALLENGE_ID", challenge.id)
        startActivity(intent)
    }
}