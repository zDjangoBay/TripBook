package com.android.tripbook

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.android.tripbook.ui.theme.TripBookTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private var generatedOTP: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        generatedOTP = generateOTP()

        setContent {
            TripBookTheme {
                OTPVerificationScreen { inputOTP ->
                    if (validateOTP(inputOTP, generatedOTP)) {
                        Toast.makeText(this, "OTP Verified Successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Invalid OTP. Try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun generateOTP(): String {
        return Random.nextInt(100000, 999999).toString()
    }

    private fun validateOTP(input: String, generated: String): Boolean {
        return input == generated
    }
}

@Composable
fun OTPVerificationScreen(onSubmit: (String) -> Unit) {
    var otpInput by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = otpInput,
                onValueChange = { otpInput = it },
                label = { Text("Enter OTP") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onSubmit(otpInput) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }
        }
    }
}
