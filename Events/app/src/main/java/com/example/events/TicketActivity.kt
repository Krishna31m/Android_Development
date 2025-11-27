package com.example.events

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.util.*

class TicketActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)

        val ticketTitle: TextView = findViewById(R.id.ticketTitle)
        val ticketUser: TextView = findViewById(R.id.ticketUser)
        val ticketDateTime: TextView = findViewById(R.id.ticketDateTime)
        val ticketVenue: TextView = findViewById(R.id.ticketVenue)
        val ticketSeatType: TextView = findViewById(R.id.ticketSeatType)
        val ticketSeats: TextView = findViewById(R.id.ticketSeats) // 🔥 Added for Number of Seats
        val ticketAmount: TextView = findViewById(R.id.ticketAmount)
        val ticketIdText: TextView = findViewById(R.id.ticketIdText)
        val qrImageView: ImageView = findViewById(R.id.ticketQRCode)

        // Retrieve booking details from Intent
        val userName = intent.getStringExtra("userName") ?: "Unknown"
        val phone = intent.getStringExtra("userPhone") ?: "Unknown"
        val eventTitle = intent.getStringExtra("eventTitle") ?: "Unknown Event"
        val eventDateTime = intent.getStringExtra("eventDateTime") ?: "Unknown Date & Time"
        val eventVenue = intent.getStringExtra("eventVenue") ?: "Unknown Venue"
        val seatType = intent.getStringExtra("seatType") ?: "Unknown Seat"
        val seats = intent.getIntExtra("seats", 1) // 🔥 Added Number of Seats
        val amount = intent.getIntExtra("amount", 0)

        // Generate a Unique Ticket ID
        val ticketId = UUID.randomUUID().toString().substring(0, 8).uppercase()

        // Set data to UI
        ticketTitle.text = eventTitle
        ticketUser.text = "Booked for: $userName\nPhone: $phone"
        ticketDateTime.text = "Date & Time: $eventDateTime"
        ticketVenue.text = "Venue: $eventVenue"
        ticketSeatType.text = "Seat Type: $seatType"
        ticketSeats.text = "Number of Seats: $seats" // 🔥 Display number of seats
        ticketAmount.text = "Total Paid: ₹$amount"
        ticketIdText.text = "Ticket ID: $ticketId"

        // Generate QR Code for Ticket
        val qrData = """
            🎟️ TICKET DETAILS 🎟️
            Ticket ID: $ticketId
            Name: $userName
            Phone: $phone
            Event: $eventTitle
            Date & Time: $eventDateTime
            Venue: $eventVenue
            Seat Type: $seatType
            Number of Seats: $seats
            Total Paid: ₹$amount
        """.trimIndent()

        val qrBitmap = generateQRCode(qrData)
        qrImageView.setImageBitmap(qrBitmap)
    }

    private fun generateQRCode(text: String): Bitmap {
        val size = 400
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        return bitmap
    }
}
