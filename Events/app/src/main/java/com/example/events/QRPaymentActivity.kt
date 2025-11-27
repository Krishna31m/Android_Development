package com.example.events

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.events.R
import com.journeyapps.barcodescanner.BarcodeEncoder

class QRPaymentActivity : Activity() {

    // UI components
    private lateinit var etName: EditText
    private lateinit var etMobile: EditText
    private lateinit var etSeats: EditText
    private lateinit var seatTypeGroup: RadioGroup
    private lateinit var countdownText: TextView
    private lateinit var qrCodeImageView: ImageView
    private lateinit var btnGenerateQR: Button

    private var totalAmount: Double = 0.0
    private var countdownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_payment)

        // Initializing UI components
        etName = findViewById(R.id.etName)
        etMobile = findViewById(R.id.etMobile)
        etSeats = findViewById(R.id.etSeats)
        seatTypeGroup = findViewById(R.id.seatTypeGroup)
        countdownText = findViewById(R.id.countdownText)
        qrCodeImageView = findViewById(R.id.qrCodeImageView)
        btnGenerateQR = findViewById(R.id.btnGenerateQR)

        // Start countdown timer when the activity starts
        startCountdownTimer()

        btnGenerateQR.setOnClickListener {
            // Calculate total amount based on user input
            calculateAmount()
            // Generate the QR code
            generateQRCode()
        }
    }

    private fun calculateAmount() {
        // Get user inputs
        val seats = etSeats.text.toString().toIntOrNull() ?: 0
        val selectedSeatTypeId = seatTypeGroup.checkedRadioButtonId
        val selectedSeatType = findViewById<RadioButton>(selectedSeatTypeId)?.text.toString()

        // Calculate total amount based on seat type
        totalAmount = when (selectedSeatType) {
            "Silver" -> seats * 100.0
            "Gold" -> seats * 200.0
            "Platinum" -> seats * 300.0
            else -> 0.0
        }

        // Display the total amount
        Toast.makeText(this, "Total: ₹$totalAmount", Toast.LENGTH_SHORT).show()
    }

    private fun generateQRCode() {
        val qrData = """
            Name: ${etName.text.toString()}
            Mobile: ${etMobile.text.toString()}
            Seats: ${etSeats.text.toString()}
            Seat Type: ${findViewById<RadioButton>(seatTypeGroup.checkedRadioButtonId).text}
            Total: ₹$totalAmount
            Event: Some Event Name
        """.trimIndent()

        val barcodeEncoder = BarcodeEncoder()
        try {
            val bitmap = barcodeEncoder.encodeBitmap(qrData, com.google.zxing.BarcodeFormat.QR_CODE, 300, 300)
            qrCodeImageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startCountdownTimer() {
        countdownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownText.text = "Time left: ${millisUntilFinished / 1000} sec"
            }

            override fun onFinish() {
                // When timer finishes, show payment confirmation dialog
                showPaymentConfirmationDialog()
            }
        }
        countdownTimer?.start()
    }

    private fun showPaymentConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Payment Confirmation")
        builder.setMessage("Have you completed the payment?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            generateTicket()  // Generate ticket on confirmation
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()  // Just dismiss the dialog if no
        }
        builder.show()
    }

    private fun generateTicket() {
        val name = etName.text.toString()
        val mobile = etMobile.text.toString()
        val seats = etSeats.text.toString()
        val selectedSeatTypeId = seatTypeGroup.checkedRadioButtonId
        val selectedSeatType = findViewById<RadioButton>(selectedSeatTypeId)?.text.toString()

        // Ticket details
        val ticketDetails = """
            Event Ticket Details:
            Name: $name
            Mobile: $mobile
            Seats: $seats
            Seat Type: $selectedSeatType
            Total Amount: ₹$totalAmount
            Event: Some Event Name
        """.trimIndent()

        // Display ticket (could be saved or sent as PDF, here it's just a Toast for now)
        Toast.makeText(this, ticketDetails, Toast.LENGTH_LONG).show()

        // Optionally, save the ticket in a file or database, or generate a PDF here
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer?.cancel()  // Cancel the countdown timer when activity is destroyed
    }
}
