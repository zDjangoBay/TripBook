package com.android.tripbook.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.tripbook.model.ReservationStatus

/**
 * Centralized icon definitions for TripBook app
 * Combines enhanced icon sets and status mapping from both branches
 */
object AppIcons {

    // Navigation icons
    val Home = Icons.Rounded.Home
    val Back = Icons.Rounded.ArrowBack
    val Close = Icons.Rounded.Close
    val Menu = Icons.Rounded.Menu
    val Search = Icons.Rounded.Search
    val Notifications = Icons.Rounded.Notifications
    val Notification = Icons.Rounded.Notifications
    val NotificationOff = Icons.Rounded.NotificationsOff
    val Profile = Icons.Rounded.Person

    // Action icons
    val Add = Icons.Rounded.Add
    val Edit = Icons.Rounded.Edit
    val Delete = Icons.Rounded.Delete
    val Share = Icons.Rounded.Share
    val Filter = Icons.Rounded.FilterList
    val Sort = Icons.Rounded.Sort
    val More = Icons.Rounded.MoreVert
    val Check = Icons.Rounded.Check
    val Clear = Icons.Rounded.Clear

    // Reservation status icons
    val CheckCircle = Icons.Rounded.CheckCircle
    val Confirmed = Icons.Rounded.CheckCircle
    val Pending = Icons.Rounded.Schedule // can also use Icons.Rounded.HourglassTop if preferred
    val Cancelled = Icons.Rounded.Cancel
    val Cancel = Icons.Rounded.Cancel
    val Completed = Icons.Rounded.Done

    // Calendar and date icons
    val Calendar = Icons.Rounded.CalendarMonth
    val CalendarToday = Icons.Rounded.CalendarToday
    val CalendarMonth = Icons.Rounded.CalendarMonth
    val CalendarWeek = Icons.Rounded.CalendarViewWeek
    val DateRange = Icons.Rounded.DateRange
    val Schedule = Icons.Rounded.Schedule
    val AccessTime = Icons.Rounded.AccessTime
    val Event = Icons.Rounded.Event
    val EventAvailable = Icons.Rounded.EventAvailable
    val EventBusy = Icons.Rounded.EventBusy

    // Travel icons
    val Flight = Icons.Rounded.Flight
    val FlightTakeoff = Icons.Filled.FlightTakeoff
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
    val LocationOn = Icons.Rounded.LocationOn
    val Location = Icons.Rounded.LocationOn
    val Map = Icons.Rounded.Map
    val Directions = Icons.Rounded.Directions

    // UI icons
    val List = Icons.Rounded.ViewList
    val Grid = Icons.Rounded.GridView
    val ArrowBack = Icons.Rounded.ArrowBack
    val ArrowForward = Icons.Rounded.ArrowForward
    val ArrowDropDown = Icons.Rounded.ArrowDropDown
    val ArrowDropUp = Icons.Rounded.ArrowDropUp
    val ExpandMore = Icons.Rounded.ExpandMore
    val ExpandLess = Icons.Rounded.ExpandLess
    val ChevronLeft = Icons.Rounded.ChevronLeft
    val ChevronRight = Icons.Rounded.ChevronRight
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
    val Payment = Icons.Rounded.Payment
    val AccountBalance = Icons.Rounded.AccountBalance
    val MonetizationOn = Icons.Rounded.MonetizationOn
    val Person = Icons.Rounded.Person
    val People = Icons.Rounded.People
    val Phone = Icons.Rounded.Phone
    val Email = Icons.Rounded.Email
    val Message = Icons.Rounded.Message
    val Chat = Icons.Rounded.Chat
    val Visibility = Icons.Rounded.Visibility
    val VisibilityOff = Icons.Rounded.VisibilityOff
    val BookOnline = Icons.Rounded.BookOnline
    val Bookmark = Icons.Rounded.Bookmark
    val BookmarkBorder = Icons.Rounded.BookmarkBorder
    val Notes = Icons.Rounded.Notes
    val Image = Icons.Rounded.Image
    val PhotoCamera = Icons.Rounded.PhotoCamera
    val Camera = Icons.Rounded.PhotoCamera
    val Photo = Icons.Rounded.Photo
    val Attachment = Icons.Rounded.AttachFile
    val Download = Icons.Rounded.Download
    val Upload = Icons.Rounded.Upload

    val Tune = Icons.Rounded.Tune
    val Settings = Icons.Rounded.Settings

    // Functions for status/travel icons
    /**
     * Get status icon based on reservation status
     */
    fun getStatusIcon(status: ReservationStatus): ImageVector {
        return when (status) {
            ReservationStatus.CONFIRMED -> CheckCircle
            ReservationStatus.PENDING -> Pending
            ReservationStatus.CANCELLED -> Cancel
            ReservationStatus.COMPLETED -> CheckCircle
        }
    }

    /**
     * Get travel type icon
     */
    fun getTravelIcon(type: String): ImageVector {
        return when (type.lowercase()) {
            "flight", "plane", "air" -> Flight
            "hotel", "accommodation" -> Hotel
            "train", "railway" -> Train
            "bus" -> Bus
            "car", "drive" -> Car
            else -> LocationOn
        }
    }
}