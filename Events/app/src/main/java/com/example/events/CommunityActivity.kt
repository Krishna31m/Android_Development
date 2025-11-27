package com.example.events

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class CommunityActivity : AppCompatActivity() {

    private lateinit var eventTitle: String
    private lateinit var postInput: EditText
    private lateinit var mediaPreview: ImageView
    private lateinit var videoPreview: VideoView
    private lateinit var selectMediaButton: Button
    private lateinit var postButton: Button
    private lateinit var postList: LinearLayout
    private lateinit var ratingBar: RatingBar
    private lateinit var ratingValue: TextView

    private var selectedMediaUri: Uri? = null
    private var isVideo: Boolean = false

    companion object {
        const val PICK_MEDIA_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        // Initialize UI components
        eventTitle = intent.getStringExtra("eventTitle") ?: "Unknown Event"
        title = "$eventTitle Community"

        postInput = findViewById(R.id.postInput)
        mediaPreview = findViewById(R.id.mediaPreview)
        videoPreview = findViewById(R.id.videoPreview)
        selectMediaButton = findViewById(R.id.selectMediaButton)
        postButton = findViewById(R.id.postButton)
        postList = findViewById(R.id.postList)
        ratingBar = findViewById(R.id.ratingBar)
        ratingValue = findViewById(R.id.ratingValue)

        // RatingBar change listener
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            ratingValue.text = "You rated this event: $rating ★"
        }

        // Apply the yellow star color to the ratingBar
        ratingBar.progressTintList = ContextCompat.getColorStateList(this, R.color.customYellow)

        // Media Picker
        selectMediaButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
            }
            startActivityForResult(intent, PICK_MEDIA_REQUEST)
        }

        // Post Button Click
        postButton.setOnClickListener {
            val postText = postInput.text.toString().trim()
            val rating = ratingBar.rating

            if (postText.isEmpty() && selectedMediaUri == null && rating == 0f) {
                Toast.makeText(this, "Please write something, select media, or rate!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addPostToCommunity(postText, selectedMediaUri, isVideo, rating)

            // Reset
            postInput.text.clear()
            ratingBar.rating = 0f
            ratingValue.text = "Rate this event"
            selectedMediaUri = null
            mediaPreview.visibility = View.GONE
            videoPreview.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_MEDIA_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedMediaUri = data?.data

            selectedMediaUri?.let { uri ->
                val type = contentResolver.getType(uri)
                isVideo = type?.startsWith("video") == true

                if (isVideo) {
                    videoPreview.setVideoURI(uri)
                    videoPreview.visibility = View.VISIBLE
                    videoPreview.setOnPreparedListener { it.isLooping = true }
                    videoPreview.start()
                    mediaPreview.visibility = View.GONE
                } else {
                    mediaPreview.setImageURI(uri)
                    mediaPreview.visibility = View.VISIBLE
                    videoPreview.visibility = View.GONE
                }
            }
        }
    }

    private fun addPostToCommunity(postText: String, mediaUri: Uri?, isVideo: Boolean, rating: Float) {
        val postView = layoutInflater.inflate(R.layout.item_community_post, postList, false)
        val textView = postView.findViewById<TextView>(R.id.postText)
        val imageView = postView.findViewById<ImageView>(R.id.postImage)
        val videoView = postView.findViewById<VideoView>(R.id.postVideo)
        val postRatingBar = postView.findViewById<RatingBar>(R.id.postRatingBar)

        textView.text = postText

        // Handle media (image/video)
        if (mediaUri != null) {
            if (isVideo) {
                videoView.setVideoURI(mediaUri)
                videoView.setOnPreparedListener { it.isLooping = true }
                videoView.visibility = View.VISIBLE
                videoView.start()
                imageView.visibility = View.GONE
            } else {
                imageView.setImageURI(mediaUri)
                imageView.visibility = View.VISIBLE
                videoView.visibility = View.GONE
            }
        } else {
            imageView.visibility = View.GONE
            videoView.visibility = View.GONE
        }

        // Set rating and apply yellow color
        postRatingBar.rating = rating
        postRatingBar.progressTintList = ContextCompat.getColorStateList(this, R.color.customYellow)

        postList.addView(postView)
    }
}
