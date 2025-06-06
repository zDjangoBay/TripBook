# TripBook
TripBook: A mobile social network for travelers exploring Africa &amp; beyond. Share stories, photos, and tips, rate travel agencies, and connect with adventurers. Community-driven platform to discover hidden gems, promote tourism, and ensure safer journeys. Built with React Native, Node.js &amp; geolocation APIs. Contributions welcome! 🌍✨
Feat: Implement CRUD Operations for Trips and Itinerary Items

This pull request introduces comprehensive Create, Read, Update, and Delete (CRUD) functionalities for both main Trip objects and their associated Itinerary Items.

Key Changes:

Trip Management (MyTripsScreen.kt & TripViewModel.kt):
Update Trip:
Added "Edit" buttons to each TripCard on MyTripsScreen.kt.
Implemented an AlertDialog for editing trip details (name, destination, dates, travelers, budget).
Integrated tripViewModel.updateTrip() call to persist changes to the Supabase backend.
Delete Trip:
Added "Delete" buttons to each TripCard on MyTripsScreen.kt.
Implemented a confirmation AlertDialog before deletion.
Integrated tripViewModel.deleteTrip() call to remove trips from the Supabase backend.
Dependencies: Ensured TripViewModel.kt contains updateTrip and deleteTrip methods to handle these operations, interacting with SupabaseTripRepository.
![Capture2.PNG](..%2F..%2FPictures%2FSaved%20Pictures%2FCapture2.PNG)
![Capture1.PNG](..%2F..%2FPictures%2FSaved%20Pictures%2FCapture1.PNG)

Itinerary Item Management (TripDetailsScreen.kt & TripDetailsViewModel.kt):
Update Itinerary Item:
Added showEditActivityDialog, hideEditActivityDialog, and selectedItineraryItem states in TripDetailsUiState to manage the editing flow.
Implemented updateItineraryItem(item: ItineraryItem) in TripDetailsViewModel.kt to handle the logic for updating an itinerary item via SupabaseTripRepository and reloading the trip details.
Delete Itinerary Item:
Added showDeleteActivityDialog, hideDeleteActivityDialog states in TripDetailsUiState for the delete confirmation flow.
Implemented deleteItineraryItem(itemId: String) in TripDetailsViewModel.kt to handle the logic for deleting an itinerary item via SupabaseTripRepository and reloading the trip details
![Capture3.PNG](..%2F..%2FPictures%2FSaved%20Pictures%2FCapture3.PNG)
![Capture.PNG](..%2F..%2FPictures%2FSaved%20Pictures%2FCapture.PNG)
