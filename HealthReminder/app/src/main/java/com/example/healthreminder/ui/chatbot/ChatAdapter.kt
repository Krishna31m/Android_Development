import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthreminder.R
import com.example.healthreminder.ui.chatbot.ChatMessage

class ChatAdapter(private val messages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_BOT = 2
        private const val VIEW_TYPE_TYPING = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            messages[position].isTyping -> VIEW_TYPE_TYPING
            messages[position].isUser -> VIEW_TYPE_USER
            else -> VIEW_TYPE_BOT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = when (viewType) {
            VIEW_TYPE_USER -> R.layout.item_chat_user
            VIEW_TYPE_TYPING -> R.layout.item_chat_typing
            else -> R.layout.item_chat_bot
        }

        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        if (!message.isTyping) {
            val tvMessage = holder.itemView.findViewById<TextView>(R.id.tv_message)
            tvMessage.text = message.message

            // Show emotion indicator for user messages
            message.emotion?.let { emotion ->
                val tvEmotion = holder.itemView.findViewById<TextView>(R.id.tv_emotion)
                tvEmotion?.visibility = View.VISIBLE
                tvEmotion?.text = getEmotionEmoji(emotion)
            }
        }
    }

    private fun getEmotionEmoji(emotion: String): String {
        return when (emotion.lowercase()) {
            "anxious", "worried" -> "😟"
            "happy", "positive" -> "😊"
            "concerned" -> "😕"
            "pain", "suffering" -> "😣"
            "confused" -> "😕"
            else -> ""
        }
    }

    override fun getItemCount() = messages.size

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun removeTypingIndicator() {
        val typingIndex = messages.indexOfFirst { it.isTyping }
        if (typingIndex != -1) {
            messages.removeAt(typingIndex)
            notifyItemRemoved(typingIndex)
        }
    }
}