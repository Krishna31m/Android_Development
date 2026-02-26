package com.krishna.varunaapp.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.krishna.varunaapp.adapters.AlertAdapter
import com.krishna.varunaapp.databinding.ActivityAlertBinding
import com.krishna.varunaapp.models.Alert

class AlertActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlertBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val alertList = mutableListOf<Alert>()
    private lateinit var adapter: AlertAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setSupportActionBar(binding.toolbarAlerts)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAlerts.setNavigationOnClickListener { finish() }

        setupRecycler()
        listenForAlerts()
    }

    private fun setupRecycler() {
        adapter = AlertAdapter(alertList)
        binding.recyclerAlerts.layoutManager = LinearLayoutManager(this)
        binding.recyclerAlerts.adapter = adapter
    }

    private fun listenForAlerts() {
        binding.progressAlerts.visibility = View.VISIBLE

        db.collection("alerts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->

                binding.progressAlerts.visibility = View.GONE

                if (error != null || snapshots == null) {
                    showEmptyState(true)
                    return@addSnapshotListener
                }

                alertList.clear()

                for (doc in snapshots.documents) {
                    val alert = doc.toObject(Alert::class.java)
                    if (alert != null) alertList.add(alert)
                }

                if (alertList.isEmpty()) {
                    showEmptyState(true)
                } else {
                    showEmptyState(false)
                    adapter.notifyDataSetChanged()
                }
            }
    }

    private fun showEmptyState(show: Boolean) {
        if (show) {
            binding.recyclerAlerts.visibility = View.GONE
            binding.tvEmptyState.visibility = View.VISIBLE
        } else {
            binding.recyclerAlerts.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE
        }
    }
}