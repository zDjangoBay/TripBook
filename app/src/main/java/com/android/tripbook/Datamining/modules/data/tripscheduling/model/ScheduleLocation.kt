package com.android.Tripbook.Datamining.modules.data.tripscheduling.model

data class ScheduleLocation(
    // This field stands for a code of the travel , as example DLA ,YDE,GAR , NGA
    val code : String,
    val name: String,// This refer to complete name of the trip schedule , for example "Gare Voyageur Olembe Yaoundé"
    val venue:String?,// This is for un embarcadère , saying the one numbered 01 with a personnel  is better than another
)
// I think this object might also help us to define des arrets intermediaires during the traveil
