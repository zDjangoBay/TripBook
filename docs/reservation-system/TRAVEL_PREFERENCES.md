# TripBook App – Travel Preferences Module (Jetpack Compose)

This module implements a complete **Travel Preferences** feature in the **TripBook** social tourism Android app using Jetpack Compose, following Material 3 design patterns.

---

## ✅ Features Implemented

- Travel Preferences Screen with full UI and ViewModel
- Supports destination types, travel style, accommodation, activities, transport, frequency, duration, and budget
- Preferences are saved and loaded using SharedPreferences
- Clean UI built using **Jetpack Compose**
- Fully integrated into the app’s existing **NavHost** structure
- Profile screen now navigates to Travel Preferences
- All screens scroll smoothly on all device sizes
- Compose widgets (`Checkbox`, `RadioButton`, `Dropdown`, `Slider`) modularized for reuse

---

## 📂 Files Included

- `TravelPreferencesScreen.kt` – main screen with top bar and scrollable form
- `PreferencesForm.kt` – reusable form with state management
- `PreferencesWidgets.kt` – composable components (checkboxes, dropdowns, radio buttons)
- `TravelPreferencesViewModel.kt` – state holder using `StateFlow`
- `TravelPreferencesRepository.kt` – persists data to SharedPreferences using Gson
- `ProfileScreen.kt` – updated with scrolling and `navController` navigation
- `DashboardActivity.kt` – updated to register the `"travel_preferences"` route

---

## 🧩 Integration Steps

### 1. Navigation

Add this to `DashboardActivity.kt`'s NavHost:

```kotlin
composable("travel_preferences") {
    TravelPreferencesScreen(onBack = { navController.popBackStack() })
}
```

### 2. Profile Option Click

In `ProfileScreen.kt`, handle:

```kotlin
"Travel Preferences" -> navController.navigate("travel_preferences")
```

### 3. Scroll Fixes

- All top-level `Column` components wrapped in `verticalScroll(rememberScrollState())`
- Fixed layout issues in `DropdownSelector` using `menuAnchor()` and `Box`

---

## 📦 Dependencies Required

Make sure your `build.gradle` includes:

```gradle
implementation 'com.google.code.gson:gson:2.10.1'
implementation "androidx.navigation:navigation-compose:2.6.0"
implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1"
```

---

## 🛠 Future Enhancements

- Add validation feedback (e.g., required fields)
- Sync preferences with a remote server
- Add confirmation dialog or snackbar on save

---
How the Travel Preferences Module Helps a Tourist
1. Personalized Travel Recommendations
By filling out their preferences — like favorite destinations, travel styles (budget, luxury), preferred transport, and activities — the app can recommend trips, packages, or experiences that match the tourist’s interests.
 Example: A user who selects mountains + hiking + camping will get different suggestions than someone who prefers cities + food tours + hotels.

2. Faster Planning and Discovery
Instead of filtering manually every time, the app remembers the traveler’s preferences. This makes trip discovery and booking faster, especially for repeat users.
 No need to reselect “Family trips” or “Beach hotels” each time — the app already knows.

3. Improved Travel Experience
By curating options that align with the user’s profile, the app reduces decision fatigue and increases satisfaction — leading to a more enjoyable and customized travel experience.
 Imagine landing in a city and immediately seeing nearby activities you love (like food tours or museums).

4. Future-Proofing for Smart Features
This preferences module lays the foundation for:

Smart itinerary generation

AI-based trip matching

Group planning with similar preferences

Notifications about ideal deals or seasonal offers

5. Trust and Comfort
When a tourist sees that the app tailors suggestions to their lifestyle, it builds user trust and loyalty — they feel understood and catered to, not just “sold to.”

In short, this module transforms TripBook from a generic booking platform into a personal travel assistant — one that knows what kind of traveler you are, and acts on it.
