package com.example.healthreminder.ui.chatbot

import ChatAdapter
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthreminder.R
import com.example.healthreminder.services.HealthChatbotService
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ChatBotActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvChat: RecyclerView
    private lateinit var etMessage: TextInputEditText
    private lateinit var btnSend: MaterialButton
    private lateinit var progressBar: ProgressBar

    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter
    private lateinit var chatbotService: HealthChatbotService

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var userHealthContext: UserHealthContext? = null
    private val conversationHistory = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize chatbot service
        chatbotService = HealthChatbotService(this)

        // Initialize views
        initializeViews()

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Health Consultant"
        toolbar.setNavigationOnClickListener { finish() }

        // Setup RecyclerView
        adapter = ChatAdapter(messages)
        rvChat.layoutManager = LinearLayoutManager(this)
        rvChat.adapter = adapter

        // Load user health context
        loadUserHealthContext()

        // Send welcome message
        sendWelcomeMessage()

        // Setup listeners
        btnSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        rvChat = findViewById(R.id.rv_chat)
        etMessage = findViewById(R.id.et_message)
        btnSend = findViewById(R.id.btn_send)
        progressBar = findViewById(R.id.progress_bar)
    }

    private fun sendWelcomeMessage() {
        val welcomeMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            message = """Hello! I'm your AI Health Consultant. 👋

I'm here to help you with:
• Health and wellness advice
• Exercise recommendations
• Diet and nutrition guidance
• Water intake tips
• Mental health support
• When to see a doctor

⚠️ Important: I don't prescribe medications or diagnose conditions. For medical treatment, please consult a licensed doctor.

How can I help you today?""",
            isUser = false,
            timestamp = Timestamp.now()
        )

        messages.add(welcomeMessage)
        adapter.notifyItemInserted(messages.size - 1)
    }

    private fun sendMessage() {
        val userText = etMessage.text.toString().trim()
        if (userText.isEmpty()) return

        // Add user message
        val userMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            message = userText,
            isUser = true,
            timestamp = Timestamp.now()
        )

        messages.add(userMessage)
        adapter.notifyItemInserted(messages.size - 1)
        rvChat.scrollToPosition(messages.size - 1)

        // Add to conversation history
        conversationHistory.add("User: $userText")

        // Clear input
        etMessage.setText("")

        // Show typing indicator
        showTypingIndicator()

        // Save message to Firestore
        saveChatMessage(userMessage)

        // Get AI response
        getBotResponse(userText)
    }

    private fun showTypingIndicator() {
        val typingMessage = ChatMessage(
            id = "typing",
            message = "",
            isUser = false,
            isTyping = true
        )
        messages.add(typingMessage)
        adapter.notifyItemInserted(messages.size - 1)
        rvChat.scrollToPosition(messages.size - 1)
    }

    private fun getBotResponse(userText: String) {
        btnSend.isEnabled = false
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // Update context with recent conversation
                val contextWithHistory = userHealthContext?.copy(
                    conversationHistory = conversationHistory.takeLast(5)
                )

                val response = chatbotService.sendMessage(userText, contextWithHistory)

                // Remove typing indicator
                adapter.removeTypingIndicator()

                if (response.success) {
                    val botMessage = ChatMessage(
                        id = System.currentTimeMillis().toString(),
                        message = response.message,
                        isUser = false,
                        timestamp = Timestamp.now(),
                        emotion = response.emotion,
                        category = response.category
                    )

                    messages.add(botMessage)
                    adapter.notifyItemInserted(messages.size - 1)
                    rvChat.scrollToPosition(messages.size - 1)

                    // Add to conversation history
                    conversationHistory.add("Assistant: ${response.message}")

                    // Save bot message
                    saveChatMessage(botMessage)
                } else {
                    showError(response.error)
                }
            } catch (e: Exception) {
                adapter.removeTypingIndicator()
                showError(e.message)
            } finally {
                btnSend.isEnabled = true
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun loadUserHealthContext() {
        val userId = auth.currentUser?.uid ?: return

        // Load user profile and health data
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val age = document.getLong("age")?.toInt()
                val gender = document.getString("gender")

                userHealthContext = UserHealthContext(
                    age = age,
                    gender = gender
                )
            }
    }

    private fun saveChatMessage(message: ChatMessage) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("chatHistory")
            .document(message.id)
            .set(message)
    }

    private fun showError(error: String?) {
        val errorMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            message = "I apologize, but I'm having trouble responding right now. Please try again. ${error ?: ""}",
            isUser = false,
            timestamp = Timestamp.now()
        )

        messages.add(errorMessage)
        adapter.notifyItemInserted(messages.size - 1)
        rvChat.scrollToPosition(messages.size - 1)
    }
}