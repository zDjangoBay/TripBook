package com.android.tripbook

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage

class SmsReceiver : BroadcastReceiver() {
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
                    
                }
            }
        }
    }
}
