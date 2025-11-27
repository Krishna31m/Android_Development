package com.example.healthreminder.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.healthreminder.R
import com.example.healthreminder.ui.auth.LoginActivity
import com.example.healthreminder.ui.emergency.EmergencyCardActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var ivProfile: CircleImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        ivProfile = view.findViewById(R.id.iv_profile)
        tvName = view.findViewById(R.id.tv_name)
        tvEmail = view.findViewById(R.id.tv_email)

        // Load user profile
        loadUserProfile()

        // Setup menu items
        setupMenuItems(view)

        return view
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        val email = auth.currentUser?.email ?: ""

        tvEmail.text = email

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "User"
                    tvName.text = name
                }
            }
    }

    private fun setupMenuItems(view: View) {
        // Edit Profile
        view.findViewById<MaterialCardView>(R.id.card_edit_profile).setOnClickListener {
            showEditProfileDialog()
        }

        // Emergency Card
        view.findViewById<MaterialCardView>(R.id.card_emergency_card).setOnClickListener {
            startActivity(Intent(context, EmergencyCardActivity::class.java))
        }

        // Notifications
        view.findViewById<MaterialCardView>(R.id.card_notifications).setOnClickListener {
            Toast.makeText(context, "Notification settings", Toast.LENGTH_SHORT).show()
        }

        // About
        view.findViewById<MaterialCardView>(R.id.card_about).setOnClickListener {
            showAboutDialog()
        }

        // Logout
        view.findViewById<MaterialCardView>(R.id.card_logout).setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_edit_profile, null)

        val etName = dialogView.findViewById<TextInputEditText>(R.id.et_name)
        val etAge = dialogView.findViewById<TextInputEditText>(R.id.et_age)
        val etWeight = dialogView.findViewById<TextInputEditText>(R.id.et_weight)
        val etHeight = dialogView.findViewById<TextInputEditText>(R.id.et_height)

        // Load current data
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                etName.setText(document.getString("name"))
                etAge.setText(document.getLong("age")?.toString())
                etWeight.setText(document.getDouble("weight")?.toString())
                etHeight.setText(document.getDouble("height")?.toString())
            }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString().trim()
                val age = etAge.text.toString().toIntOrNull() ?: 0
                val weight = etWeight.text.toString().toFloatOrNull() ?: 0f
                val height = etHeight.text.toString().toFloatOrNull() ?: 0f

                val updates = hashMapOf(
                    "name" to name,
                    "age" to age,
                    "weight" to weight,
                    "height" to height
                )

                firestore.collection("users")
                    .document(userId)
                    .update(updates as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                        loadUserProfile()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("About Health Reminder")
            .setMessage(
                "Version: 1.0.0\n\n" +
                        "Health Reminder helps you manage your health by providing:\n" +
                        "• Medicine reminders\n" +
                        "• Water intake tracking\n" +
                        "• Exercise routines\n" +
                        "• Diet planning\n" +
                        "• Health challenges\n\n" +
                        "Stay healthy! 💚"
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                auth.signOut()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity?.finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}