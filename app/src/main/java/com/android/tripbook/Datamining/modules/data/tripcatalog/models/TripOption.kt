package com.android.Tripbook.Datamining.modules.data.tripcatalog.models

data class TripOption(
                      val id: Int,
                      val name: String,
                      val description: String,
                      val price: Double,
                      val isSelected: Boolean = false
                      )
