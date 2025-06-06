package com.android.tripbook

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.tripbook.data.Trip
import com.android.tripbook.data.TripDatabaseHelper
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AddTripActivity : AppCompatActivity() {

    private lateinit var etTripTitle: TextInputEditText
    private lateinit var etDestination: TextInputEditText
    private lateinit var btnStartDate: Button
    private lateinit var btnEndDate: Button
    private lateinit var spinnerTripType: Spinner
    private lateinit var etAgency: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button

    private lateinit var dbHelper: TripDatabaseHelper
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    private var startDate: Date? = null
    private var endDate: Date? = null
    private var preselectedDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_trip)

        // Get preselected date from intent if available
        val preselectedDateMillis = intent.getLongExtra("selected_date", -1)
        if (preselectedDateMillis != -1L) {
            preselectedDate = Date(preselectedDateMillis)
            startDate = preselectedDate
        }

        initViews()
        setupDatabase()
        setupSpinner()
        setupDatePickers()
        setupButtons()
    }

    private fun initViews() {
        etTripTitle = findViewById(R.id.et_trip_title)
        etDestination = findViewById(R.id.et_destination)
        btnStartDate = findViewById(R.id.btn_start_date)
        btnEndDate = findViewById(R.id.btn_end_date)
        spinnerTripType = findViewById(R.id.spinner_trip_type)
        etAgency = findViewById(R.id.et_agency)
        etDescription = findViewById(R.id.et_description)
        btnCancel = findViewById(R.id.btn_cancel)
        btnSave = findViewById(R.id.btn_save)

        // Set initial start date if preselected
        preselectedDate?.let {
            btnStartDate.text = dateFormat.format(it)
        }
    }

    private fun setupDatabase() {
        dbHelper = TripDatabaseHelper(this)
    }

    private fun setupSpinner() {
        val tripTypes =
            arrayOf("Adventure", "Leisure", "Business", "Cultural", "Medical", "Educational")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tripTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTripType.adapter = adapter
    }

    private fun setupDatePickers() {
        btnStartDate.setOnClickListener {
            showDatePicker { selectedDate ->
                startDate = selectedDate
                btnStartDate.text = dateFormat.format(selectedDate)

                // Auto-set end date to same day if not already set
                if (endDate == null) {
                    endDate = selectedDate
                    btnEndDate.text = dateFormat.format(selectedDate)
                }
            }
        }

        btnEndDate.setOnClickListener {
            showDatePicker { selectedDate ->
                if (startDate != null && selectedDate.before(startDate)) {
                    Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    endDate = selectedDate
                    btnEndDate.text = dateFormat.format(selectedDate)
                }
            }
        }
    }

    private fun setupButtons() {
        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            saveTrip()
        }
    }

    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun saveTrip() {
        val title = etTripTitle.text.toString().trim()
        val destination = etDestination.text.toString().trim()
        val agency = etAgency.text.toString().trim().takeIf { it.isNotEmpty() }
        val description = etDescription.text.toString().trim().takeIf { it.isNotEmpty() }
        val tripType = spinnerTripType.selectedItem.toString()

        // Validation
        if (title.isEmpty()) {
            etTripTitle.error = "Trip title is required"
            etTripTitle.requestFocus()
            return
        }

        if (destination.isEmpty()) {
            etDestination.error = "Destination is required"
            etDestination.requestFocus()
            return
        }

        if (startDate == null) {
            Toast.makeText(this, "Please select start date", Toast.LENGTH_SHORT).show()
            return
        }

        if (endDate == null) {
            Toast.makeText(this, "Please select end date", Toast.LENGTH_SHORT).show()
            return
        }

        // Calculate duration
        val diffInMillis = endDate!!.time - startDate!!.time
        val duration = (diffInMillis / (1000 * 60 * 60 * 24)).toInt() + 1

        // Create trip object
        val trip = Trip(
            title = title,
            destination = destination,
            startDate = startDate!!,
            endDate = endDate!!,
            duration = duration,
            type = tripType,
            description = description,
            agency = agency
        )

        // Save to database
        val result = dbHelper.insertTrip(trip)

        if (result != -1L) {
            Toast.makeText(this, "Trip saved successfully!", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Error saving trip", Toast.LENGTH_SHORT).show()
        }
    }
}