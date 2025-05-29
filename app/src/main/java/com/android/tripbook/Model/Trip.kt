package com.android.tripbook.Model

import java.io.Serializable

data class Trip(
    val companyLogo:String="",
    val companyName:String="",
    val arriveTime: String="",
    val date:String="",
    val from: String="",
    val fromshort:String="",
    val price:Double=0.0,
    val time:String="",
    val to:String="",
    val score:Int=0,
    val toshort:String="",
): Serializable
