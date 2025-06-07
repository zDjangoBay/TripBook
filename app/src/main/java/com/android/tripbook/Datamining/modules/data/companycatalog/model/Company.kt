package com.android.companycatalog.model

import java.util.UUID
import kotlinx.serialization.Serializable

@Serializable
data class Company(
    val Company_Id:String=UUID.randomUUID().toString(),
    val CompanyName:String,
    val CompanyMain_id:String,// This right here is the code of the Main company , so let's say United Voyages , it will be UnitedVoy458 for instance and the same for the other
    val CompanyLocalisation:String,// Here it is about knowing if the company is at Nyom or at Mvan
    val CompanyAgency:String,// Here i wanted to illustrated , let's say the company Touristique has a succursale at Douala , at Garoua , Youade and Bertoua
    val CompanyScore:Int?,//This will be the score given by the users to the company that is going to be compiled based on the number of reviews
    val CompanyStatus: CompanyStatus,
)
