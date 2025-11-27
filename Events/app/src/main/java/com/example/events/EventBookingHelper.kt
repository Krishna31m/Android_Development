package com.example.events

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

class EventBookingHelper(
    private val context: Context,
    private val eventTitle: String,
    private val eventDateTime: String,
    private val eventVenue: String,
    private val priceRange: String
) {
    fun showBookingDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_booking, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.nameInput)
        val phoneInput = dialogView.findViewById<EditText>(R.id.phoneInput)
        val seatsInput = dialogView.findViewById<EditText>(R.id.seatsInput)
        val seatTypeSpinner = dialogView.findViewById<Spinner>(R.id.seatTypeSpinner)
        val totalAmountText = dialogView.findViewById<TextView>(R.id.totalAmountText)

        val prices = priceRange.replace("₹", "").split("-").map { it.trim().replace(",", "").toInt() }
        val minPrice = prices.getOrElse(0) { 0 }
        val maxPrice = prices.getOrElse(1) { minPrice }

        val silverPrice = minPrice
        val goldPrice = (minPrice + maxPrice) / 2
        val platinumPrice = maxPrice

        val seatTypes = arrayOf(
            "Silver - ₹$silverPrice",
            "Gold - ₹$goldPrice",
            "Platinum - ₹$platinumPrice"
        )

        val seatPrices = mapOf(
            seatTypes[0] to silverPrice,
            seatTypes[1] to goldPrice,
            seatTypes[2] to platinumPrice
        )

        seatTypeSpinner.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, seatTypes)

        fun updateTotalAmount() {
            val seats = seatsInput.text.toString().toIntOrNull() ?: 0
            val seatType = seatTypeSpinner.selectedItem.toString()
            val pricePerSeat = seatPrices[seatType] ?: 0
            val totalAmount = pricePerSeat * seats
            totalAmountText.text = "Total Amount: ₹$totalAmount"
        }

        seatsInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = updateTotalAmount()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        seatTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateTotalAmount()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        AlertDialog.Builder(context)
            .setTitle("Book Tickets")
            .setView(dialogView)
            .setPositiveButton("Pay Now") { _, _ ->
                val name = nameInput.text.toString().trim()
                val phone = phoneInput.text.toString().trim()
                val seats = seatsInput.text.toString().toIntOrNull() ?: 0
                val seatType = seatTypeSpinner.selectedItem.toString()
                val pricePerSeat = seatPrices[seatType] ?: 0
                val totalAmount = pricePerSeat * seats

                if (name.isEmpty() || phone.isEmpty() || seats <= 0) {
                    Toast.makeText(context, "Please fill all details correctly.", Toast.LENGTH_SHORT).show()
                } else {
                    showQRCodeDialog(name, phone, seats, seatType, totalAmount)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun showQRCodeDialog(userName: String, phone: String, seats: Int, seatType: String, amount: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_qr_code, null)
        val qrImageView = dialogView.findViewById<ImageView>(R.id.qrCodeImageView)
        val countdownTextView = dialogView.findViewById<TextView>(R.id.countdownTextView)

        val upiId = "krishnamukhiya842-1@oksbi"
        val paymentLink = "upi://pay?pa=$upiId&pn=Krishna&am=$amount&cu=INR"
        val qrBitmap = generateQRCode(paymentLink)
        qrImageView.setImageBitmap(qrBitmap)

        qrImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentLink))
            intent.setPackage("com.google.android.apps.nbu.paisa.user") // Optional: Force open in GPay
            context.startActivity(intent)
        }

        val dialog = AlertDialog.Builder(context)
            .setTitle("Scan & Pay")
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.show()

        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownTextView.text = "Time left: ${millisUntilFinished / 1000} sec"
            }

            override fun onFinish() {
                dialog.dismiss()
                confirmPaymentManually(userName, phone, seats, seatType, amount)
            }
        }.start()
    }

    private fun confirmPaymentManually(userName: String, phone: String, seats: Int, seatType: String, amount: Int) {
        AlertDialog.Builder(context)
            .setTitle("Payment Confirmation")
            .setMessage("Did you complete the payment?")
            .setPositiveButton("Yes") { _, _ ->
                Toast.makeText(context, "✅ Payment Successful! Generating Ticket...", Toast.LENGTH_SHORT).show()

                val intent = Intent(context, TicketActivity::class.java).apply {
                    putExtra("userName", userName)
                    putExtra("userPhone", phone)
                    putExtra("eventTitle", eventTitle)
                    putExtra("eventDateTime", eventDateTime)
                    putExtra("eventVenue", eventVenue)
                    putExtra("seatType", seatType)
                    putExtra("amount", amount)
                    putExtra("seats", seats)
                }
                context.startActivity(intent)
            }
            .setNegativeButton("No") { _, _ ->
                Toast.makeText(context, "❌ Payment failed. Please try again.", Toast.LENGTH_LONG).show()
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
