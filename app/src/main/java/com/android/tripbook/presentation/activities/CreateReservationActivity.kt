package com.android.tripbook.presentation.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.tripbook.R
import com.android.tripbook.data.models.ReservationRequest
import com.android.tripbook.data.models.ReservationType
import com.android.tripbook.presentation.viewmodels.ReservationViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateReservationActivity : AppCompatActivity() {
    
    private val viewModel: ReservationViewModel by viewModels()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var checkInDate: Date? = null
    private var checkOutDate: Date? = null
    
    // UI Components
    private lateinit var spinnerType: Spinner
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextLocation: EditText
    private lateinit var buttonCheckIn: Button
    private lateinit var buttonCheckOut: Button
    private lateinit var editTextGuests: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var spinnerCurrency: Spinner
    private lateinit var editTextProvider: EditText
    private lateinit var editTextContact: EditText
    private lateinit var editTextRequests: EditText
    private lateinit var buttonCreate: Button
    private lateinit var progressBar: ProgressBar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Note: This would need an actual layout file
        // setContentView(R.layout.activity_create_reservation)
        
        initializeViews()
        setupSpinners()
        setupDatePickers()
        setupObservers()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        // In a real implementation, these would be initialized from the layout
        // For now, we'll create them programmatically as a basic example
        
        spinnerType = Spinner(this)
        editTextTitle = EditText(this)
        editTextDescription = EditText(this)
        editTextLocation = EditText(this)
        buttonCheckIn = Button(this)
        buttonCheckOut = Button(this)
        editTextGuests = EditText(this)
        editTextPrice = EditText(this)
        spinnerCurrency = Spinner(this)
        editTextProvider = EditText(this)
        editTextContact = EditText(this)
        editTextRequests = EditText(this)
        buttonCreate = Button(this)
        progressBar = ProgressBar(this)
        
        // Set default values
        buttonCheckIn.text = "Select Check-in Date"
        buttonCheckOut.text = "Select Check-out Date (Optional)"
        buttonCreate.text = "Create Reservation"
        editTextGuests.setText("1")
        editTextPrice.setText("0.0")
    }
    
    private fun setupSpinners() {
        // Setup reservation type spinner
        val typeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            ReservationType.values().map { it.name.replace("_", " ").lowercase().capitalize() }
        )
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = typeAdapter
        
        // Setup currency spinner
        val currencies = arrayOf("XAF", "USD", "EUR", "GBP")
        val currencyAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            currencies
        )
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency.adapter = currencyAdapter
        spinnerCurrency.setSelection(0) // Default to XAF
    }
    
    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()
        
        buttonCheckIn.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    checkInDate = calendar.time
                    buttonCheckIn.text = dateFormat.format(checkInDate!!)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        
        buttonCheckOut.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    checkOutDate = calendar.time
                    buttonCheckOut.text = dateFormat.format(checkOutDate!!)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
    
    private fun setupClickListeners() {
        buttonCreate.setOnClickListener {
            createReservation()
        }
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                progressBar.visibility = if (state.isCreatingReservation) 
                    android.view.View.VISIBLE else android.view.View.GONE
                
                buttonCreate.isEnabled = !state.isCreatingReservation
                
                if (state.reservationCreated) {
                    Toast.makeText(this@CreateReservationActivity, 
                        "Reservation created successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                
                state.errorMessage?.let { error ->
                    Toast.makeText(this@CreateReservationActivity, error, Toast.LENGTH_LONG).show()
                    viewModel.clearError()
                }
            }
        }
    }
    
    private fun createReservation() {
        try {
            // Validate inputs
            val title = editTextTitle.text.toString().trim()
            val location = editTextLocation.text.toString().trim()
            val provider = editTextProvider.text.toString().trim()
            val guests = editTextGuests.text.toString().toIntOrNull() ?: 1
            val price = editTextPrice.text.toString().toDoubleOrNull() ?: 0.0
            
            if (title.isEmpty()) {
                editTextTitle.error = "Title is required"
                return
            }
            
            if (location.isEmpty()) {
                editTextLocation.error = "Location is required"
                return
            }
            
            if (provider.isEmpty()) {
                editTextProvider.error = "Provider name is required"
                return
            }
            
            if (checkInDate == null) {
                Toast.makeText(this, "Please select a check-in date", Toast.LENGTH_SHORT).show()
                return
            }
            
            val reservationRequest = ReservationRequest(
                userId = "current_user_id", // This should come from user session
                type = ReservationType.values()[spinnerType.selectedItemPosition],
                title = title,
                description = editTextDescription.text.toString().takeIf { it.isNotBlank() },
                location = location,
                checkInDate = checkInDate!!,
                checkOutDate = checkOutDate,
                guestCount = guests,
                totalPrice = price,
                currency = spinnerCurrency.selectedItem.toString(),
                providerName = provider,
                providerContact = editTextContact.text.toString().takeIf { it.isNotBlank() },
                specialRequests = editTextRequests.text.toString().takeIf { it.isNotBlank() }
            )
            
            viewModel.createReservation(reservationRequest)
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error creating reservation: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}