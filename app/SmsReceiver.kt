package com.android.tripbook

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {
    private val TAG = "SmsReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val pdus = bundle.get("pdus") as? Array<*>
            val smsMessages: Array<SmsMessage> = pdus?.mapNotNull { pdu ->
                // Create SmsMessage from the PDU
                if (pdu is ByteArray) {
                    SmsMessage.createFromPdu(pdu)
                } else {
                    null
                }
            }?.toTypedArray() ?: return

            for (smsMessage in smsMessages) {
                val messageBody = smsMessage.messageBody 
                if (messageBody != null) {
                    Log.d(TAG, "Received SMS: $messageBody")

                    // Extract OTP from the message body
                    val otp = extractOTP(messageBody)
                    if (otp != null) {
                        // Notify user about the received OTP
                        Toast.makeText(context, "Your OTP is: $otp", Toast.LENGTH_LONG).show()

                        // Here you can also trigger an activity to handle OTP verification
                        // For example, starting an OTP verification activity
                        val verifyIntent = Intent(context, OtpVerificationActivity::class.java)
                        verifyIntent.putExtra("otp", otp)
                        verifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(verifyIntent)
                    }
                }
            }
        }
    }

    private fun extractOTP(message: String): String? {
        // Assuming the OTP is a 6-digit number
        val otpRegex = Regex("\\b\\d{6}\\b")
        val matchResult = otpRegex.find(message)
        return matchResult?.value // Return the matched OTP or null if not found
    }
}
