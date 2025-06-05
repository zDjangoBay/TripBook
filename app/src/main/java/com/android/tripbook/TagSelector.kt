package com.android.tripbook

import com.android.tripbook.model.Place


class TagSelector {
    private val selectedPlaces = mutableSetOf<Place>()

    fun togglePlaceSelection(place: Place) {
        if (selectedPlaces.contains(place)) {
            selectedPlaces.remove(place)
        } else {
            selectedPlaces.add(place)
        }
    }

    fun getSelectedPlaces(): Set<Place> = selectedPlaces
}
