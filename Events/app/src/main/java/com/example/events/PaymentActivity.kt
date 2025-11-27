package com.example.events

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PaymentActivity : AppCompatActivity() {

    companion object {
        const val PAYMENT_REQUEST_CODE = 200
    }

    private var bookingAmount: Int = 0
    private var bookingName: String = ""
    private var bookingPhone: String = ""
    private var bookingSeats: Int = 0
    private var bookingSeatType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // UI Elements
        val payButton: Button = findViewById(R.id.pay_button)
        val payQRButton: Button = findViewById(R.id.pay_qr_button)
        val qrImage: ImageView = findViewById(R.id.qr_image)
        val transactionInput: EditText = findViewById(R.id.transaction_id_input)
        val submitButton: Button = findViewById(R.id.submit_transaction)

        // Get booking details from intent
        bookingAmount = intent.getIntExtra("amount", 0)
        bookingName = intent.getStringExtra("name") ?: "Unknown"
        bookingPhone = intent.getStringExtra("phone") ?: "Unknown"
        bookingSeats = intent.getIntExtra("seats", 0)
        bookingSeatType = intent.getStringExtra("seatType") ?: "Unknown"

        payButton.setOnClickListener {
            initiateUPIPayment()
        }

        payQRButton.setOnClickListener {
            // Show QR code, input, and submit button
            qrImage.visibility = ImageView.VISIBLE
            transactionInput.visibility = EditText.VISIBLE
            submitButton.visibility = Button.VISIBLE

            // Here, you can generate the QR code
            generateQRCode()
        }

        submitButton.setOnClickListener {
            val txnId = transactionInput.text.toString()
            if (txnId.length == 12) {
                // Send this to owner for validation
                validateTransactionWithOwner(txnId)
            } else {
                Toast.makeText(this, "Please enter a valid 12-digit Transaction ID.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateQRCode() {
        // Here, you can generate the QR code using a library like ZXing or any other QR generation library
        // For now, we will just simulate QR code generation
        val fakeQRCodeUri = "https://example.com/qr-code"
        // Generate QR code for fake URL
        // qrImage.setImageBitmap(generateQRCodeBitmap(fakeQRCodeUri))
    }

    private fun initiateUPIPayment() {
        val upiId = "krishnamukhiya842-1@oksbi"  // Replace with your actual UPI ID
        val name = "EmperorHotel"
        val note = "Booking Payment"
        val amount = bookingAmount.toString()

        val uri = Uri.parse(
            "upi://pay?pa=$upiId&pn=$name&mc=1234&tr=${generateTransactionReference()}&tn=$note&am=$amount&cu=INR"
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = uri
        }

        val activities = packageManager.queryIntentActivities(intent, 0)
        if (activities.isNotEmpty()) {
            val chooser = Intent.createChooser(intent, "Pay with UPI")
            startActivityForResult(chooser, PAYMENT_REQUEST_CODE)
        } else {
            Toast.makeText(this, "No UPI app found on the device.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateTransactionReference(): String {
        return "TXN" + System.currentTimeMillis().toString().takeLast(7)
    }

    private fun validateTransactionWithOwner(txnId: String) {
        // Simulate owner validation
        Toast.makeText(this, "Transaction ID submitted. Awaiting owner verification...", Toast.LENGTH_LONG).show()
    }
}
