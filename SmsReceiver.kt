package com.tripbook.otp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage

class SmsReceiver(private val onOtpReceived: (String) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras ?: return
        val pdus = bundle.get("pdus") as? Array<*> ?: return
        for (pdu in pdus) {
            val sms = SmsMessage.createFromPdu(pdu as ByteArray)
            val message = sms.messageBody
            val otp = Regex("\\d{6}").find(message)?.value
            if (otp != null) {
                onOtpReceived(otp)
                break
            }
        }
    }
}