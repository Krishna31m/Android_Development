package com.example.events

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

class KrishnaPaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_krishna_payment)

        val userName = intent.getStringExtra("userName") ?: ""
        val userPhone = intent.getStringExtra("userPhone") ?: ""
        val seatType = intent.getStringExtra("seatType") ?: ""
        val seats = intent.getIntExtra("seats", 0)
        val amount = intent.getIntExtra("amount", 0)

        val qrImageView = findViewById<ImageView>(R.id.qrCodeImageView)
        val countdownTextView = findViewById<TextView>(R.id.countdownTextView)
        val payNowBtn = findViewById<Button>(R.id.payNowButton)

        val upiId = "krishnamukhiya842-1@oksbi"
        val paymentLink = "upi://pay?pa=$upiId&pn=Krishna&am=$amount&cu=INR"

        qrImageView.setImageBitmap(generateQRCode(paymentLink))

        payNowBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentLink))
            intent.setPackage("com.google.android.apps.nbu.paisa.user") // GPay
            startActivity(intent)
        }

        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownTextView.text = "Time left: ${millisUntilFinished / 1000} sec"
            }

            override fun onFinish() {
                confirmPaymentDialog(userName, userPhone, seats, seatType, amount)
            }
        }.start()
    }

    private fun confirmPaymentDialog(userName: String, phone: String, seats: Int, seatType: String, amount: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Payment")
            .setMessage("Did you complete the payment of ₹$amount?")
            .setPositiveButton("Yes") { _, _ ->
                Toast.makeText(this, "✅ Payment Successful!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, TicketActivity::class.java).apply {
                    putExtra("userName", userName)
                    putExtra("userPhone", phone)
                    putExtra("seatType", seatType)
                    putExtra("amount", amount)
                    putExtra("seats", seats)
                }
                startActivity(intent)
            }
            .setNegativeButton("No") { _, _ ->
                Toast.makeText(this, "❌ Payment not completed.", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun generateQRCode(text: String): Bitmap {
        val size = 500
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        return bitmap
    }
}
