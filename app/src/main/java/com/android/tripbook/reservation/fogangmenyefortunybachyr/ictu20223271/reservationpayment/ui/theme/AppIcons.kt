package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Custom icon set for the TripBook app
 * Provides a centralized place for all icons used in the app
 */
object AppIcons {
    // Navigation icons
    val Back = Icons.Rounded.ArrowBack
    val Close = Icons.Rounded.Close
    val Menu = Icons.Rounded.Menu
    
    // Action icons
    val Add = Icons.Rounded.Add
    val Edit = Icons.Rounded.Edit
    val Delete = Icons.Rounded.Delete
    val Share = Icons.Rounded.Share
    val Filter = Icons.Rounded.FilterList
    val Search = Icons.Rounded.Search
    val Sort = Icons.Rounded.Sort
    val More = Icons.Rounded.MoreVert
    
    // Reservation status icons
    val Confirmed = Icons.Rounded.CheckCircle
    val Pending = Icons.Rounded.HourglassTop
    val Cancelled = Icons.Rounded.Cancel
    val Completed = Icons.Rounded.Done
    
    // Calendar icons
    val Calendar = Icons.Rounded.CalendarMonth
    val CalendarToday = Icons.Rounded.Today
    val CalendarWeek = Icons.Rounded.CalendarViewWeek
    val CalendarMonth = Icons.Rounded.CalendarViewMonth
    val Event = Icons.Rounded.Event
    val EventAvailable = Icons.Rounded.EventAvailable
    val EventBusy = Icons.Rounded.EventBusy
    
    // Travel icons
    val Flight = Icons.Rounded.Flight
    val FlightTakeoff = Icons.Rounded.FlightTakeoff
    val FlightLand = Icons.Rounded.FlightLand
    val Hotel = Icons.Rounded.Hotel
    val Restaurant = Icons.Rounded.Restaurant
    val Beach = Icons.Rounded.BeachAccess
    val Hiking = Icons.Rounded.Terrain
    val Car = Icons.Rounded.DirectionsCar
    val Train = Icons.Rounded.Train
    val Bus = Icons.Rounded.DirectionsBus
    val Taxi = Icons.Rounded.LocalTaxi
    val Boat = Icons.Rounded.DirectionsBoat
    val Bike = Icons.Rounded.DirectionsBike
    
    // UI icons
    val List = Icons.Rounded.ViewList
    val Grid = Icons.Rounded.GridView
    val Map = Icons.Rounded.Map
    val Settings = Icons.Rounded.Settings
    val Favorite = Icons.Rounded.Favorite
    val FavoriteBorder = Icons.Rounded.FavoriteBorder
    val Star = Icons.Rounded.Star
    val StarBorder = Icons.Rounded.StarBorder
    val Info = Icons.Rounded.Info
    val Help = Icons.Rounded.Help
    val Warning = Icons.Rounded.Warning
    val Error = Icons.Rounded.Error
    
    // Weather icons
    val Sunny = Icons.Rounded.WbSunny
    val Cloudy = Icons.Rounded.Cloud
    val Rainy = Icons.Filled.Umbrella
    val Snowy = Icons.Filled.AcUnit
    val Windy = Icons.Filled.Air
    
    // Misc icons
    val Money = Icons.Rounded.AttachMoney
    val CreditCard = Icons.Rounded.CreditCard
    val Person = Icons.Rounded.Person
    val People = Icons.Rounded.People
    val Phone = Icons.Rounded.Phone
    val Email = Icons.Rounded.Email
    val Location = Icons.Rounded.LocationOn
    val Directions = Icons.Rounded.Directions
    val Camera = Icons.Rounded.PhotoCamera
    val Photo = Icons.Rounded.Photo
    val Bookmark = Icons.Rounded.Bookmark
    val BookmarkBorder = Icons.Rounded.BookmarkBorder
    val Notes = Icons.Rounded.Notes
    val Notification = Icons.Rounded.Notifications
    val NotificationOff = Icons.Rounded.NotificationsOff
    
    /**
     * Get the appropriate icon for a reservation status
     */
    fun getStatusIcon(status: com.android.tripbook.model.ReservationStatus): ImageVector {
        return when (status) {
            com.android.tripbook.model.ReservationStatus.CONFIRMED -> Confirmed
            com.android.tripbook.model.ReservationStatus.PENDING -> Pending
            com.android.tripbook.model.ReservationStatus.CANCELLED -> Cancelled
            com.android.tripbook.model.ReservationStatus.COMPLETED -> Completed
        }
    }
}
