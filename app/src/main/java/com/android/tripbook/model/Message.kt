package com.android.tripbook.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Message(
    @get:PropertyName("id") @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("text") @set:PropertyName("text")
    var text: String = "",

    @get:PropertyName("senderId") @set:PropertyName("senderId")
    var senderId: String = "",

    @get:PropertyName("senderName") @set:PropertyName("senderName")
    var senderName: String = "",

    @get:PropertyName("tripId") @set:PropertyName("tripId")
    var tripId: String = "",

    @get:PropertyName("timestamp") @set:PropertyName("timestamp")
    @ServerTimestamp
    var timestamp: Timestamp? = null
) {
    // No-argument constructor required by Firestore
    constructor() : this("", "", "", "", "", null)
}