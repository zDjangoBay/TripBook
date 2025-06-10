package com.android.tripbook.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.tripbook.model.ReservationStatus

/**
 * Centralized icon definitions for TripBook app
 */
object AppIcons {

    // Navigation icons
    val Home = Icons.Rounded.Home
    val Search = Icons.Rounded.Search
    val Notifications = Icons.Rounded.Notifications
    val Profile = Icons.Rounded.Person

    // Action icons
    val Add = Icons.Rounded.Add
    val Edit = Icons.Rounded.Edit
    val Delete = Icons.Rounded.Delete
    val Share = Icons.Rounded.Share
    val Filter = Icons.Rounded.FilterList
    val Sort = Icons.Rounded.Sort
    val More = Icons.Rounded.MoreVert
    val Close = Icons.Rounded.Close
    val Check = Icons.Rounded.Check
    val Clear = Icons.Rounded.Clear

    // Calendar and date icons
    val Calendar = Icons.Rounded.CalendarToday
    val CalendarToday = Icons.Rounded.CalendarToday
    val CalendarMonth = Icons.Rounded.CalendarMonth
    val DateRange = Icons.Rounded.DateRange
    val Schedule = Icons.Rounded.Schedule
    val AccessTime = Icons.Rounded.AccessTime

    // Travel icons
    val Flight = Icons.Rounded.Flight
    val FlightTakeoff = Icons.Filled.FlightTakeoff
    val Hotel = Icons.Rounded.Hotel
    val LocationOn = Icons.Rounded.LocationOn
    val Location = Icons.Rounded.LocationOn
    val Map = Icons.Rounded.Map
    val Directions = Icons.Rounded.Directions
    val Train = Icons.Rounded.Train
    val DirectionsBus = Icons.Rounded.DirectionsBus
    val DirectionsCar = Icons.Rounded.DirectionsCar

    // Status icons
    val CheckCircle = Icons.Rounded.CheckCircle
    val Pending = Icons.Rounded.Schedule
    val Cancel = Icons.Rounded.Cancel
    val Warning = Icons.Rounded.Warning
    val Error = Icons.Rounded.Error
    val Info = Icons.Rounded.Info

    // UI icons
    val ArrowBack = Icons.Rounded.ArrowBack
    val ArrowForward = Icons.Rounded.ArrowForward
    val ArrowDropDown = Icons.Rounded.ArrowDropDown
    val ArrowDropUp = Icons.Rounded.ArrowDropUp
    val ExpandMore = Icons.Rounded.ExpandMore
    val ExpandLess = Icons.Rounded.ExpandLess
    val ChevronLeft = Icons.Rounded.ChevronLeft
    val ChevronRight = Icons.Rounded.ChevronRight

    // Content icons
    val Image = Icons.Rounded.Image
    val PhotoCamera = Icons.Rounded.PhotoCamera
    val Attachment = Icons.Rounded.AttachFile
    val Download = Icons.Rounded.Download
    val Upload = Icons.Rounded.Upload
    val List = Icons.Filled.List
    val Money = Icons.Filled.Money
    val Notes = Icons.Filled.Notes

    // Communication icons
    val Email = Icons.Rounded.Email
    val Phone = Icons.Rounded.Phone
    val Message = Icons.Rounded.Message
    val Chat = Icons.Rounded.Chat

    // Settings icons
    val Settings = Icons.Rounded.Settings
    val Tune = Icons.Rounded.Tune
    val Visibility = Icons.Rounded.Visibility
    val VisibilityOff = Icons.Rounded.VisibilityOff

    // Payment icons
    val Payment = Icons.Rounded.Payment
    val CreditCard = Icons.Rounded.CreditCard
    val AccountBalance = Icons.Rounded.AccountBalance
    val MonetizationOn = Icons.Rounded.MonetizationOn

    // Booking icons
    val BookOnline = Icons.Rounded.BookOnline
    val Bookmark = Icons.Rounded.Bookmark
    val BookmarkBorder = Icons.Rounded.BookmarkBorder
    val Favorite = Icons.Rounded.Favorite
    val FavoriteBorder = Icons.Rounded.FavoriteBorder

    // Additional icons for enhanced functionality
    val Photo = Icons.Rounded.Photo
    val Notification = Icons.Rounded.Notifications
    val NotificationOff = Icons.Rounded.NotificationsOff

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
            "bus" -> DirectionsBus
            "car", "drive" -> DirectionsCar
            else -> LocationOn
        }
    }
}
