package com.android.tripbook.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.data.models.Hotel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.android.tripbook.R



class MockHotelRepository @Inject constructor() {
    private val dummyHotels = listOf(
        Hotel("h1", "Grand Hyatt", "Paris, France", "$250 - $400", R.drawable.hotel_placeholder, 4.5, "2024-07-01", "A luxurious hotel in the heart of Paris, offering stunning views of the Eiffel Tower and world-class amenities. Perfect for a romantic getaway or a family vacation."),
        Hotel("h2", "City View Inn", "Rome, Italy", "$180 - $300", R.drawable.hotel_placeholder, 4.2, "2024-07-15", "Comfortable and affordable accommodation in Rome, close to major historical sites like the Colosseum and Roman Forum. Ideal for budget-conscious travelers."),
        Hotel("h3", "Beach Paradise Resort", "Bali, Indonesia", "$300 - $600", R.drawable.hotel_placeholder, 4.8, "2024-08-01", "An idyllic beachfront resort in Bali, surrounded by lush tropical gardens and pristine beaches. Enjoy spa treatments, exquisite dining, and breathtaking sunsets."),
        Hotel("h4", "Mountain Retreat Lodge", "Swiss Alps", "$400 - $800", R.drawable.hotel_placeholder, 4.9, "2024-08-10", "Experience the serene beauty of the Swiss Alps in this charming mountain lodge. Perfect for hiking, skiing, and enjoying nature's tranquility."),
    )

    fun getHotelById(id: String): Hotel? {
        return dummyHotels.find { it.id == id }
    }
}


@HiltViewModel
class HotelDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val hotelRepository: MockHotelRepository // Inject the mock repository
) : ViewModel() {

    private val _hotel = MutableLiveData<Hotel?>()
    val hotel: LiveData<Hotel?> = _hotel

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadHotelDetails(hotelId: String) {
        viewModelScope.launch {
            try {
                val foundHotel = hotelRepository.getHotelById(hotelId)
                if (foundHotel != null) {
                    _hotel.value = foundHotel
                } else {
                    _errorMessage.value = "Hotel with ID $hotelId not found."
                    _hotel.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading hotel details: ${e.message}"
                _hotel.value = null
            }
        }
    }
}