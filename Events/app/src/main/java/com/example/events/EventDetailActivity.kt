package com.example.events

import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

class EventDetailActivity : AppCompatActivity() {

    companion object {
        const val PAYMENT_REQUEST_CODE = 200
    }

    private var eventTitle = ""
    private var eventDateTime = ""
    private var eventVenue = ""
    private var eventDescription = ""

    private var bookingName = ""
    private var bookingPhone = ""
    private var bookingSeats = 0
    private var bookingSeatType = ""
    private var bookingAmount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        val eventImage: ImageView = findViewById(R.id.eventImageDetail)
        val eventTitleView: TextView = findViewById(R.id.eventTitleDetail)
        val eventArtist: TextView = findViewById(R.id.eventArtist)
        val eventDateTimeView: TextView = findViewById(R.id.eventDateTime)
        val eventVenueView: TextView = findViewById(R.id.eventVenue)
        val eventPrice: TextView = findViewById(R.id.eventPrice)
        val eventDescriptionView: TextView = findViewById(R.id.eventDescriptionDetail)
        val bookNowButton: Button = findViewById(R.id.bookNowButton)
        val shareButton: Button = findViewById(R.id.shareButton)
        val joinCommunityButton: Button = findViewById(R.id.joinCommunityButton)

        eventImage.setImageResource(intent.getIntExtra("imageResId", 0))
        eventTitle = intent.getStringExtra("title") ?: "Untitled"
        eventDateTime = intent.getStringExtra("dateTime") ?: "TBD"
        eventVenue = intent.getStringExtra("venue") ?: "TBD"
        val artist = intent.getStringExtra("artist") ?: "Unknown"
        val price = intent.getStringExtra("price") ?: "0"
        eventDescription = intent.getStringExtra("description") ?: "No Description"

        eventTitleView.text = eventTitle
        eventArtist.text = "Artist: $artist"
        eventDateTimeView.text = "Date & Time: $eventDateTime"
        eventVenueView.text = "Venue: $eventVenue"
        eventPrice.text = "Ticket Price: ₹$price"
        eventDescriptionView.text = eventDescription

        bookNowButton.setOnClickListener { showBookingDialog() }
        shareButton.setOnClickListener { shareEvent() }
        joinCommunityButton.setOnClickListener {
            val communityIntent = Intent(this, CommunityActivity::class.java)
            communityIntent.putExtra("eventTitle", eventTitle)
            startActivity(communityIntent)
        }

//        val otherPaymentBtn: Button = findViewById(R.id.btnOtherPaymentOption)
//
//        otherPaymentBtn.setOnClickListener {
//            val intent = Intent(this, KrishnaPaymentActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun shareEvent() {
        val shareText = """
            🎫 *$eventTitle*
            📍 Venue: $eventVenue
            📅 Date & Time: $eventDateTime
            📄 Details: $eventDescription
            👉 Book your tickets now on the EventHub app!
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val chooser = Intent.createChooser(sendIntent, "Share Event via")
        startActivity(chooser)

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Event Details", shareText)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Event link copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    private fun showBookingDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_booking, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.nameInput)
        val phoneInput = dialogView.findViewById<EditText>(R.id.phoneInput)
        val seatsInput = dialogView.findViewById<EditText>(R.id.seatsInput)
        val seatTypeSpinner = dialogView.findViewById<Spinner>(R.id.seatTypeSpinner)
        val totalAmountText = dialogView.findViewById<TextView>(R.id.totalAmountText)

        val priceRange = intent.getStringExtra("price") ?: "₹0 - ₹0"
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

        seatTypeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, seatTypes)

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

        AlertDialog.Builder(this)
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
                    Toast.makeText(this, "Please fill all details correctly.", Toast.LENGTH_SHORT).show()
                } else {
                    bookingName = name
                    bookingPhone = phone
                    bookingSeats = seats
                    bookingSeatType = seatType
                    bookingAmount = totalAmount
                    showPaymentOptionsDialog()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun showPaymentOptionsDialog() {
        val options = arrayOf("Google Pay", "Paytm", "UPI ID", "QR Code")
        AlertDialog.Builder(this)
            .setTitle("Select Payment Option")
            .setItems(options) { _, which ->
                val method = options[which]
                if (method == "QR Code") {
                    showQRCodePayment()
                } else {
                    initiatePayment(method)
                }
            }
            .show()
    }

    private fun initiatePayment(method: String) {
        val upiId = "paytm.s1hvmkd@pty"
        val name = "EmperorHotel"
        val note = "Payment for booking tickets"
        val amount = bookingAmount.toString()

        val uri = Uri.parse("upi://pay?pa=$upiId&pn=$name&mc=0000&tid=1234567890&tn=$note&am=$amount&cu=INR")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        val chooser = Intent.createChooser(intent, "Pay with...")
        startActivityForResult(chooser, PAYMENT_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PAYMENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val response = data.getStringExtra("response") ?: return
                val responseMap = response.split("&").associate {
                    val parts = it.split("=")
                    if (parts.size == 2) parts[0] to parts[1] else "" to ""
                }

                val status = responseMap["Status"] ?: responseMap["status"]
                val approvalRef = responseMap["ApprovalRefNo"] ?: responseMap["approvalRefNo"]

                if (status.equals("SUCCESS", ignoreCase = true)) {
                    Toast.makeText(this, "Payment successful! Ref: $approvalRef", Toast.LENGTH_LONG).show()
                    generateTicket()
                } else {
                    Toast.makeText(this, "Payment failed or canceled", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No response received", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showQRCodePayment() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_qr_code_payment, null)
        val qrCodeImage: ImageView = dialogView.findViewById(R.id.qrCodeImage)
        val timerText: TextView = dialogView.findViewById(R.id.timerText)

        val qrContent = "upi://pay?pa=paytm.s1hvmkd@pty&pn=EmperorHotel&am=$bookingAmount&cu=INR"
        val qrBitmap = generateQRCode(qrContent)
        qrCodeImage.setImageBitmap(qrBitmap)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Scan & Pay")
            .setView(dialogView)
            .setCancelable(false)
            .create()

        alertDialog.show()

        // Start 1-minute countdown
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                timerText.text = "Time remaining: $seconds sec"
            }

            override fun onFinish() {
                alertDialog.dismiss()

                // Ask if payment was completed
                AlertDialog.Builder(this@EventDetailActivity)
                    .setTitle("Payment Completed?")
                    .setMessage("Did you complete the payment by scanning the QR code?")
                    .setPositiveButton("Yes") { _, _ ->
                        // Ask for Transaction ID
                        val inputView = EditText(this@EventDetailActivity)
                        inputView.hint = "Enter 12-digit Transaction ID"
                        inputView.inputType = android.text.InputType.TYPE_CLASS_NUMBER

                        AlertDialog.Builder(this@EventDetailActivity)
                            .setTitle("Enter Transaction ID")
                            .setView(inputView)
                            .setPositiveButton("Submit") { _, _ ->
                                val transactionId = inputView.text.toString().trim()
                                if (transactionId.length == 12) {
                                    validateTransaction(transactionId)
                                } else {
                                    Toast.makeText(this@EventDetailActivity, "Invalid Transaction ID", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }.start()
    }


    private fun generateQRCode(content: String): Bitmap {
        val writer = MultiFormatWriter()
        val bitMatrix: BitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 500, 500)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    private fun validateTransaction(transactionId: String) {
        // Dummy validation - you can replace this with real backend verification
        Toast.makeText(this, "Transaction ID $transactionId validated successfully", Toast.LENGTH_SHORT).show()
        generateTicket()
    }

    private fun generateTicket() {
        val ticketView = LayoutInflater.from(this).inflate(R.layout.ticket_layout, null)

        val titleView = ticketView.findViewById<TextView>(R.id.ticketTitle)
        val eventTitleView = ticketView.findViewById<TextView>(R.id.ticketEventTitle)
        val dateTimeView = ticketView.findViewById<TextView>(R.id.ticketDateTime)
        val venueView = ticketView.findViewById<TextView>(R.id.ticketVenue)
        val nameView = ticketView.findViewById<TextView>(R.id.ticketName)
        val phoneView = ticketView.findViewById<TextView>(R.id.ticketPhone)
        val seatsView = ticketView.findViewById<TextView>(R.id.ticketSeats)
        val seatTypeView = ticketView.findViewById<TextView>(R.id.ticketSeatType)
        val amountView = ticketView.findViewById<TextView>(R.id.ticketAmount)
        val closeBtn = ticketView.findViewById<Button>(R.id.closeTicketBtn)

        // Set values
        eventTitleView.text = "Event: $eventTitle"
        dateTimeView.text = "Date & Time: $eventDateTime"
        venueView.text = "Venue: $eventVenue"
        nameView.text = "Name: $bookingName"
        phoneView.text = "Phone: $bookingPhone"
        seatsView.text = "Seats: $bookingSeats"
        seatTypeView.text = "Seat Type: $bookingSeatType"
        amountView.text = "Total Paid: ₹$bookingAmount"

        val dialog = AlertDialog.Builder(this)
            .setView(ticketView)
            .setCancelable(false)
            .create()

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}
