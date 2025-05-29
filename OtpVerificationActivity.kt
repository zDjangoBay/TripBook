package com.tripbook.otp

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tripbook.R

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var otpEditText: EditText
    private lateinit var sendOtpButton: Button
    private lateinit var verifyOtpButton: Button
    private var generatedOtp: String = ""
    private lateinit var smsReceiver: SmsReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        otpEditText = findViewById(R.id.otpEditText)
        sendOtpButton = findViewById(R.id.sendOtpButton)
        verifyOtpButton = findViewById(R.id.verifyOtpButton)

        sendOtpButton.setOnClickListener {
            generatedOtp = OtpGenerator.generateOtp()
            Toast.makeText(this, "Your OTP is $generatedOtp", Toast.LENGTH_LONG).show()
            // In production, send OTP via backend/SMS gateway
        }

        verifyOtpButton.setOnClickListener {
            val enteredOtp = otpEditText.text.toString()
            if (enteredOtp == generatedOtp) {
                Toast.makeText(this, "OTP Verified!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }

        // SMS Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), 1)
        }

        // SMS Receiver
        smsReceiver = SmsReceiver { otp ->
            otpEditText.setText(otp)
        }
        registerReceiver(smsReceiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsReceiver)
    }
}