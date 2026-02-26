package com.krishna.varunaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.krishna.varunaapp.databinding.ItemInformationPostBinding
import com.krishna.varunaapp.models.InformationPost
import java.text.SimpleDateFormat
import java.util.*

class InformationAdapter(
    private val posts: List<InformationPost>
) : RecyclerView.Adapter<InformationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemInformationPostBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInformationPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]

        holder.binding.tvHeading.text = post.heading
        holder.binding.tvDescription.text = post.description
        holder.binding.tvDate.text = formatDate(post.createdAt)

        // Load image with Glide
        if (!post.imageUrl.isNullOrEmpty()) {
            holder.binding.ivPostImage.visibility = View.VISIBLE
            holder.binding.progressImage.visibility = View.VISIBLE

            // Convert Google Drive links to direct download links if needed
            val imageUrl = convertGoogleDriveUrl(post.imageUrl)

            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.binding.progressImage.visibility = View.GONE
                        holder.binding.ivPostImage.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.binding.progressImage.visibility = View.GONE
                        return false
                    }
                })
                .into(holder.binding.ivPostImage)
        } else {
            holder.binding.ivPostImage.visibility = View.GONE
            holder.binding.progressImage.visibility = View.GONE
        }
    }

    override fun getItemCount() = posts.size

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Converts Google Drive sharing links to direct download links
     */
    private fun convertGoogleDriveUrl(url: String): String {
        return when {
            url.contains("drive.google.com/file/d/") -> {
                // Extract file ID from URL
                val fileIdPattern = "file/d/([^/]+)".toRegex()
                val matchResult = fileIdPattern.find(url)
                matchResult?.let {
                    val fileId = it.groupValues[1]
                    "https://drive.google.com/uc?export=view&id=$fileId"
                } ?: url
            }
            url.contains("drive.google.com/open?id=") -> {
                // Extract file ID from open link
                val fileIdPattern = "id=([^&]+)".toRegex()
                val matchResult = fileIdPattern.find(url)
                matchResult?.let {
                    val fileId = it.groupValues[1]
                    "https://drive.google.com/uc?export=view&id=$fileId"
                } ?: url
            }
            else -> url
        }
    }
}


//package com.krishna.varunaapp.adapters
//
//import android.graphics.BitmapFactory
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.krishna.varunaapp.databinding.ItemInformationPostBinding
//import com.krishna.varunaapp.models.InformationPost
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.net.URL
//import java.text.SimpleDateFormat
//import java.util.*
//
//class InformationAdapter(
//    private val posts: List<InformationPost>
//) : RecyclerView.Adapter<InformationAdapter.ViewHolder>() {
//
//    inner class ViewHolder(val binding: ItemInformationPostBinding) :
//        RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ItemInformationPostBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val post = posts[position]
//
//        holder.binding.tvHeading.text = post.heading
//        holder.binding.tvDescription.text = post.description
//        holder.binding.tvDate.text = formatDate(post.createdAt)
//
//        // Load image if URL is provided
//        if (!post.imageUrl.isNullOrEmpty()) {
//            holder.binding.ivPostImage.visibility = View.VISIBLE
//            holder.binding.progressImage.visibility = View.VISIBLE
//
//            // Load image in background thread
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val url = URL(post.imageUrl)
//                    val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
//
//                    withContext(Dispatchers.Main) {
//                        holder.binding.progressImage.visibility = View.GONE
//                        if (bitmap != null) {
//                            holder.binding.ivPostImage.setImageBitmap(bitmap)
//                        } else {
//                            holder.binding.ivPostImage.visibility = View.GONE
//                        }
//                    }
//                } catch (e: Exception) {
//                    withContext(Dispatchers.Main) {
//                        holder.binding.progressImage.visibility = View.GONE
//                        holder.binding.ivPostImage.visibility = View.GONE
//                    }
//                }
//            }
//        } else {
//            holder.binding.ivPostImage.visibility = View.GONE
//            holder.binding.progressImage.visibility = View.GONE
//        }
//    }
//
//    override fun getItemCount() = posts.size
//
//    private fun formatDate(timestamp: Long): String {
//        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
//        return sdf.format(Date(timestamp))
//    }
//}
//


//package com.krishna.varunaapp.adapters
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.krishna.varunaapp.databinding.ItemInformationPostBinding
//import com.krishna.varunaapp.models.InformationPost
//import java.text.SimpleDateFormat
//import java.util.*
//
//class InformationAdapter(
//    private val posts: List<InformationPost>
//) : RecyclerView.Adapter<InformationAdapter.ViewHolder>() {
//
//    inner class ViewHolder(val binding: ItemInformationPostBinding) :
//        RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ItemInformationPostBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val post = posts[position]
//
//        holder.binding.tvHeading.text = post.heading
//        holder.binding.tvDescription.text = post.description
//        holder.binding.tvDate.text = formatDate(post.createdAt)
//
//        // Hide image section since we're not using Glide
//        // Users can still provide image URLs, but they won't be displayed
//        holder.binding.ivPostImage.visibility = View.GONE
//    }
//
//    override fun getItemCount() = posts.size
//
//    private fun formatDate(timestamp: Long): String {
//        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
//        return sdf.format(Date(timestamp))
//    }
//}

//package com.krishna.varunaapp.adapters
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.krishna.varunaapp.R
//import com.krishna.varunaapp.databinding.ItemInformationPostBinding
//import com.krishna.varunaapp.models.InformationPost
//import java.text.SimpleDateFormat
//import java.util.*
//
//class InformationAdapter(
//    private val posts: List<InformationPost>
//) : RecyclerView.Adapter<InformationAdapter.ViewHolder>() {
//
//    inner class ViewHolder(val binding: ItemInformationPostBinding) :
//        RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ItemInformationPostBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val post = posts[position]
//
//        holder.binding.tvHeading.text = post.heading
//        holder.binding.tvDescription.text = post.description
//        holder.binding.tvDate.text = formatDate(post.createdAt)
//
//        // Show image if URL is provided
//        if (!post.imageUrl.isNullOrEmpty()) {
//            holder.binding.ivPostImage.visibility = View.VISIBLE
//            Glide.with(holder.itemView.context)
//                .load(post.imageUrl)
//                .placeholder(R.drawable.ic_placeholder)
//                .error(R.drawable.ic_error)
//                .into(holder.binding.ivPostImage)
//        } else {
//            holder.binding.ivPostImage.visibility = View.GONE
//        }
//    }
//
//    override fun getItemCount() = posts.size
//
//    private fun formatDate(timestamp: Long): String {
//        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
//        return sdf.format(Date(timestamp))
//    }
//}