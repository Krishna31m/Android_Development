package com.example.events

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener

class OtherBookingActivity : AppCompatActivity() {

    private lateinit var countdownText: TextView
    private lateinit var bookNowButton: Button
    private var countdown: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_booking)

        countdownText = findViewById(R.id.countdownText)
        bookNowButton = findViewById(R.id.bookNowButton)

        startCountdownTimer()

        bookNowButton.setOnClickListener {
            showBookingDialog()
        }
    }

    private fun startCountdownTimer() {
        countdown = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                countdownText.text = "Time Remaining: $secondsRemaining seconds"
            }

            override fun onFinish() {
                countdownText.text = "Time's up! Please try again."
                bookNowButton.isEnabled = false
            }
        }.start()
    }

    private fun showBookingDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_booking, null)

        val nameInput = dialogView.findViewById<EditText>(R.id.nameInput)
        val phoneInput = dialogView.findViewById<EditText>(R.id.phoneInput)
        val seatsInput = dialogView.findViewById<EditText>(R.id.seatsInput)
        val seatTypeSpinner = dialogView.findViewById<Spinner>(R.id.seatTypeSpinner)
        val totalAmountText = dialogView.findViewById<TextView>(R.id.totalAmountText)

        val priceRange = intent.getStringExtra("price") ?: "₹100 - ₹300"
        val prices = priceRange.replace("₹", "").split("-").map { it.trim().replace(",", "").toInt() }
        val minPrice = prices.getOrElse(0) { 100 }
        val maxPrice = prices.getOrElse(1) { 300 }

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

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, seatTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        seatTypeSpinner.adapter = adapter

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Enter Booking Details")
            .setPositiveButton("Proceed", null)
            .setNegativeButton("Cancel", null)

        val dialog = dialogBuilder.create()
        dialog.show()

        val proceedButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        proceedButton.setOnClickListener {
            val name = nameInput.text.toString()
            val phone = phoneInput.text.toString()
            val seats = seatsInput.text.toString().toIntOrNull() ?: 0
            val selectedSeatType = seatTypeSpinner.selectedItem.toString()
            val pricePerSeat = seatPrices[selectedSeatType] ?: 0
            val totalAmount = seats * pricePerSeat

            if (name.isNotEmpty() && phone.isNotEmpty() && seats > 0) {
                val intent = Intent(this, ManualQRPaymentActivity::class.java)
                intent.putExtra("userName", name)
                intent.putExtra("mobile", phone)
                intent.putExtra("seatCount", seats)
                intent.putExtra("seatType", selectedSeatType.split(" - ")[0])
                intent.putExtra("totalAmount", totalAmount)
                intent.putExtra("eventTitle", "Event XYZ")
                intent.putExtra("eventDate", "10 May 2025")
                intent.putExtra("eventVenue", "ABC Ground")
                startActivity(intent)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }

        seatsInput.addTextChangedListener {
            val seatCount = it.toString().toIntOrNull() ?: 0
            val price = seatPrices[seatTypeSpinner.selectedItem.toString()] ?: 0
            totalAmountText.text = "Total Amount: ₹${seatCount * price}"
        }

        seatTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val seatCount = seatsInput.text.toString().toIntOrNull() ?: 0
                val selected = seatTypeSpinner.selectedItem.toString()
                val price = seatPrices[selected] ?: 0
                totalAmountText.text = "Total Amount: ₹${seatCount * price}"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}
