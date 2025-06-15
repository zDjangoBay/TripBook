package com.tripbook.userprofilesunjo.presentation.auth.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tripbook.userprofilesunjo.presentation.navigation.Screen
import com.tripbook.userprofilesunjo.presentation.viewmodel.PasswordRecoveryViewModel
import com.tripbook.userprofilesunjo.util.OtpStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: PasswordRecoveryViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Navigate to OTP verification when email is sent successfully
    LaunchedEffect(message) {
        if (message.contains("sent successfully") && !isError) {
            kotlinx.coroutines.delay(2000) // Show success message for 2 seconds
            navController.navigate("${Screen.VerifyOtp.route}/$email")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forgot Password") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            Text(
                text = "Reset Your Password",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Enter your email address and we'll send you a verification code to reset your password.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    message = ""
                    isError = false
                },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = isError,
                supportingText = {
                    if (message.isNotEmpty()) {
                        Text(
                            text = message,
                            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Send Code Button
            Button(
                onClick = {
                    if (email.isBlank()) {
                        isError = true
                        message = "Please enter your email address"
                        return@Button
                    }

                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        isError = true
                        message = "Please enter a valid email address"
                        return@Button
                    }

                    isLoading = true
                    message = ""
                    isError = false

                    coroutineScope.launch {
                        try {
                            val success = withContext(Dispatchers.IO) {
                                sendPasswordResetEmail(email, context)
                            }

                            isLoading = false
                            if (success) {
                                isError = false
                                message = "Verification code sent successfully! Check your email."
                            } else {
                                isError = true
                                message = "Failed to send email. Please try again."
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            isError = true
                            message = "Error: ${e.localizedMessage ?: "Failed to send email"}"
                        }
                    }
                },
                enabled = !isLoading && email.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text("Sending...")
                    }
                } else {
                    Text(
                        text = "Send Verification Code",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Back to Login
            TextButton(
                onClick = { navController.popBackStack() }
            ) {
                Text("Back to Login")
            }

            // Instructions Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "How it works:",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "• Enter your real email address\n• Check your email for a 6-digit verification code\n• Enter the code in the next step to reset your password",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

private suspend fun sendPasswordResetEmail(toEmail: String, context: Context): Boolean {
    return try {
        // Generate a random 6-digit OTP
        val otp = (100000..999999).random().toString()

        // Store the OTP for verification
        OtpStorage.storeOtp(toEmail, otp)

        val properties = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                // SECURITY WARNING: Never hardcode credentials in production!
                // Use environment variables or secure storage
                return PasswordAuthentication("byronbright2k21@gmail.com", "ruvy meea gssh yjdq")
            }
        })

        val message = MimeMessage(session).apply {
            setFrom(InternetAddress("byronbright2k21@gmail.com", "TripBook Password Recovery"))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
            subject = "TripBook Password Reset Code"

            // Create HTML email content
            setContent(
                """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <h2 style="color: #6750A4;">🔐 TripBook Password Reset</h2>
                        <p>Hello,</p>
                        <p>You requested to reset your password for your TripBook account. Use the verification code below:</p>
                        
                        <div style="background-color: #EADDff; padding: 20px; text-align: center; margin: 20px 0; border-radius: 12px; border: 2px solid #6750A4;">
                            <h1 style="color: #6750A4; font-size: 36px; margin: 0; letter-spacing: 8px; font-weight: bold;">$otp</h1>
                        </div>
                        
                        <p><strong>⏰ This code will expire in 10 minutes.</strong></p>
                        
                        <p>If you didn't request this password reset, please ignore this email or contact support if you have concerns.</p>
                        
                        <div style="background-color: #f8f9fa; padding: 15px; border-radius: 8px; margin: 20px 0;">
                            <p style="margin: 0; font-size: 14px; color: #666;">
                                <strong>🛡️ Security Note:</strong> Never share this code with anyone. TripBook will never ask for your verification code via phone or email.
                            </p>
                        </div>
                        
                        <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;">
                        <p style="font-size: 12px; color: #666;">
                            This is an automated message from TripBook Password Recovery System.<br>
                            Please do not reply to this email.
                        </p>
                    </div>
                </body>
                </html>
            """.trimIndent(), "text/html"
            )
        }

        Transport.send(message)
        true
    } catch (e: MessagingException) {
        e.printStackTrace()
        false
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
