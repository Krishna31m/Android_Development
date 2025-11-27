package com.example.events

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ManualTicketActivity : AppCompatActivity() {

    private lateinit var ticketDetailsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_ticket)

        ticketDetailsText = findViewById(R.id.ticketDetailsText)

        val name = intent.getStringExtra("name")
        val phone = intent.getStringExtra("phone")
        val seats = intent.getIntExtra("seats", 0)
        val seatType = intent.getStringExtra("seatType")
        val amount = intent.getIntExtra("amount", 0)
        val txnId = intent.getStringExtra("txnId")
        val eventTitle = intent.getStringExtra("eventTitle")

        val ticketDetails = """
            Event: $eventTitle
            Name: $name
            Phone: $phone
            Seats: $seats ($seatType)
            Total Amount: ₹$amount
            Transaction ID: $txnId
        """.trimIndent()

        ticketDetailsText.text = ticketDetails
    }
}
