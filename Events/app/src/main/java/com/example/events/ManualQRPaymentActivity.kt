package com.example.events

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

class ManualQRPaymentActivity : AppCompatActivity() {

    private lateinit var qrImage: ImageView
    private lateinit var timerText: TextView
    private lateinit var statusText: TextView

    private var totalAmount = 0
    private lateinit var userName: String
    private lateinit var mobile: String
    private lateinit var seatType: String
    private var seatCount = 0
    private lateinit var eventTitle: String
    private lateinit var eventDate: String
    private lateinit var eventVenue: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_qr_payment)

        qrImage = findViewById(R.id.qrImage)
        timerText = findViewById(R.id.timerText)
        statusText = findViewById(R.id.statusText)

        // Get passed data
        totalAmount = intent.getIntExtra("totalAmount", 0)
        userName = intent.getStringExtra("userName") ?: ""
        mobile = intent.getStringExtra("mobile") ?: ""
        seatType = intent.getStringExtra("seatType") ?: ""
        seatCount = intent.getIntExtra("seatCount", 0)
        eventTitle = intent.getStringExtra("eventTitle") ?: ""
        eventDate = intent.getStringExtra("eventDate") ?: ""
        eventVenue = intent.getStringExtra("eventVenue") ?: ""

        statusText.text = "Pay ₹$totalAmount by scanning this QR code"

        // Generate the QR code and display it
        val qrCodeData = "Payment for event: $eventTitle\nAmount: ₹$totalAmount\nUser: $userName"
        val qrBitmap = generateQRCode(qrCodeData)
        qrImage.setImageBitmap(qrBitmap)

        // Start 1-minute timer
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = "Time left: ${millisUntilFinished / 1000} sec"
            }

            override fun onFinish() {
                showPaymentConfirmDialog()
            }
        }.start()
    }

    private fun generateQRCode(content: String): Bitmap {
        val writer = MultiFormatWriter()
        val bitMatrix: BitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 500, 500)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) -0x1000000 else -0x1)
            }
        }
        return bmp
    }

    private fun showPaymentConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("Payment Confirmation")
            .setMessage("Have you completed the payment?")
            .setPositiveButton("Yes") { _, _ ->
                openTicketScreen()
            }
            .setNegativeButton("No") { _, _ ->
                Toast.makeText(this, "Payment not completed.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun openTicketScreen() {
        val intent = Intent(this, TicketActivity::class.java)
        intent.putExtra("userName", userName)
        intent.putExtra("mobile", mobile)
        intent.putExtra("seatType", seatType)
        intent.putExtra("seatCount", seatCount)
        intent.putExtra("eventTitle", eventTitle)
        intent.putExtra("eventDate", eventDate)
        intent.putExtra("eventVenue", eventVenue)
        intent.putExtra("totalAmount", totalAmount)
        startActivity(intent)
        finish()
    }
}
