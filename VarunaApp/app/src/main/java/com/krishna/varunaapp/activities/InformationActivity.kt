package com.krishna.varunaapp.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.krishna.varunaapp.adapters.InformationAdapter
import com.krishna.varunaapp.databinding.ActivityInformationBinding
import com.krishna.varunaapp.models.InformationPost

class InformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInformationBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val posts = mutableListOf<InformationPost>()
    private lateinit var adapter: InformationAdapter

    private var userRole = "GeneralUser"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setSupportActionBar(binding.toolbarInformation)
        binding.toolbarInformation.setNavigationOnClickListener { finish() }

        setupRecycler()
        loadUserRole()
        loadPosts()

        binding.btnPublish.setOnClickListener { publishPost() }
    }

    private fun setupRecycler() {
        adapter = InformationAdapter(posts)
        binding.recyclerPosts.layoutManager = LinearLayoutManager(this)
        binding.recyclerPosts.adapter = adapter
    }

    private fun loadUserRole() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener {
                userRole = it.getString("role") ?: "GeneralUser"

                // Only admins/authorized users can create posts
                if (userRole == "GeneralUser") {
                    binding.layoutCreatePost.visibility = View.GONE
                }
            }
    }

    private fun publishPost() {
        val heading = binding.etHeading.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val imageUrl = binding.etImageUrl.text.toString().trim()

        if (heading.isEmpty()) {
            Toast.makeText(this, "Heading is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Description is required", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressPublish.visibility = View.VISIBLE

        val id = db.collection("information_posts").document().id

        val post = InformationPost(
            id = id,
            heading = heading,
            description = description,
            imageUrl = imageUrl.ifEmpty { null },
            createdAt = System.currentTimeMillis(),
            createdBy = auth.currentUser?.uid ?: ""
        )

        db.collection("information_posts").document(id)
            .set(post)
            .addOnSuccessListener {
                // Create alert notification for all users
                createAlertForNewPost(post)

                binding.progressPublish.visibility = View.GONE
                Toast.makeText(this, "Post published successfully", Toast.LENGTH_SHORT).show()
                clearFields()
                loadPosts()
            }
            .addOnFailureListener {
                binding.progressPublish.visibility = View.GONE
                Toast.makeText(this, "Failed to publish post", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createAlertForNewPost(post: InformationPost) {
        val alertId = db.collection("alerts").document().id

        val alert = hashMapOf(
            "id" to alertId,
            "title" to "New Information Posted",
            "message" to post.heading,
            "type" to "info",
            "relatedPostId" to post.id,
            "createdAt" to System.currentTimeMillis(),
            "createdBy" to (auth.currentUser?.uid ?: "")
        )

        db.collection("alerts").document(alertId).set(alert)
    }

    private fun loadPosts() {
        db.collection("information_posts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, error ->
                if (snap == null || error != null) return@addSnapshotListener

                posts.clear()
                for (doc in snap.documents) {
                    val post = doc.toObject(InformationPost::class.java)
                    if (post != null) posts.add(post)
                }

                adapter.notifyDataSetChanged()
            }
    }

    private fun clearFields() {
        binding.etHeading.text?.clear()
        binding.etDescription.text?.clear()
        binding.etImageUrl.text?.clear()
    }
}

//package com.krishna.varunaapp.activities
//
//import android.os.Bundle
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.Query
//import com.krishna.varunaapp.adapters.InformationAdapter
//import com.krishna.varunaapp.databinding.ActivityInformationBinding
//import com.krishna.varunaapp.models.InformationPost
//
//class InformationActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityInformationBinding
//    private lateinit var db: FirebaseFirestore
//    private lateinit var auth: FirebaseAuth
//
//    private val posts = mutableListOf<InformationPost>()
//    private lateinit var adapter: InformationAdapter
//
//    private var userRole = "GeneralUser"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityInformationBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        db = FirebaseFirestore.getInstance()
//        auth = FirebaseAuth.getInstance()
//
//        setSupportActionBar(binding.toolbarInformation)
//        binding.toolbarInformation.setNavigationOnClickListener { finish() }
//
//        setupRecycler()
//        loadUserRole()
//        loadPosts()
//
//        binding.btnPublish.setOnClickListener { publishPost() }
//    }
//
//    private fun setupRecycler() {
//        adapter = InformationAdapter(posts)
//        binding.recyclerPosts.layoutManager = LinearLayoutManager(this)
//        binding.recyclerPosts.adapter = adapter
//    }
//
//    private fun loadUserRole() {
//        val uid = auth.currentUser?.uid ?: return
//        db.collection("users").document(uid).get()
//            .addOnSuccessListener {
//                userRole = it.getString("role") ?: "GeneralUser"
//
//                // Only admins/authorized users can create posts
//                if (userRole == "GeneralUser") {
//                    binding.layoutCreatePost.visibility = View.GONE
//                }
//            }
//    }
//
//    private fun publishPost() {
//        val heading = binding.etHeading.text.toString().trim()
//        val description = binding.etDescription.text.toString().trim()
//        val imageUrl = binding.etImageUrl.text.toString().trim()
//
//        if (heading.isEmpty()) {
//            Toast.makeText(this, "Heading is required", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (description.isEmpty()) {
//            Toast.makeText(this, "Description is required", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        binding.progressPublish.visibility = View.VISIBLE
//
//        val id = db.collection("information_posts").document().id
//
//        val post = InformationPost(
//            id = id,
//            heading = heading,
//            description = description,
//            imageUrl = imageUrl.ifEmpty { null },
//            createdAt = System.currentTimeMillis(),
//            createdBy = auth.currentUser?.uid ?: ""
//        )
//
//        db.collection("information_posts").document(id)
//            .set(post)
//            .addOnSuccessListener {
//                binding.progressPublish.visibility = View.GONE
//                Toast.makeText(this, "Post published successfully", Toast.LENGTH_SHORT).show()
//                clearFields()
//                loadPosts()
//            }
//            .addOnFailureListener {
//                binding.progressPublish.visibility = View.GONE
//                Toast.makeText(this, "Failed to publish post", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun loadPosts() {
//        db.collection("information_posts")
//            .orderBy("createdAt", Query.Direction.DESCENDING)
//            .addSnapshotListener { snap, error ->
//                if (snap == null || error != null) return@addSnapshotListener
//
//                posts.clear()
//                for (doc in snap.documents) {
//                    val post = doc.toObject(InformationPost::class.java)
//                    if (post != null) posts.add(post)
//                }
//
//                adapter.notifyDataSetChanged()
//            }
//    }
//
//    private fun clearFields() {
//        binding.etHeading.text?.clear()
//        binding.etDescription.text?.clear()
//        binding.etImageUrl.text?.clear()
//    }
//}