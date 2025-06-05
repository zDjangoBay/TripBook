package com.android.tripbook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.model.Place

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.placeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val places = listOf(
            Place(
                name = "Mount Cameroon",
                imageUrl = "https://th.bing.com/th/id/R.3585c45e528cb251f7940fa087d6aacd?rik=FeLrqXywj088RQ&pid=ImgRaw&r=0",
                location = "Cameroon"
            ),
            Place(
                name = "Limbe Beach",
                imageUrl = "https://thumbs.dreamstime.com/b/dramatic-coast-rocky-volcanic-beach-green-tree-waves-amazing-sunset-limbe-cameroon-83611760.jpg",
                location = "Limbe, Cameroon"
            ),
            Place(
                name = "Bafut Palace",
                imageUrl = "https://c8.alamy.com/comp/HG7F6A/museum-at-traditional-palace-of-the-fon-of-bafut-with-brick-and-tile-HG7F6A.jpg",
                location = "Bafut, Cameroon"
            ),
            Place(
                name = "Waza National Park",
                imageUrl = "https://live.staticflickr.com/4358/35885262124_90847e4aaf_b.jpg",
                location = "Waza, Cameroon"
            ),
            Place(
                name = "Menchum Falls",
                imageUrl = "https://live.staticflickr.com/2824/11585108044_04b526707b_b.jpg",
                location = "Menchum, Cameroon"
            ),
            Place(
                name = "Kribi Beach",
                imageUrl = "https://i.pinimg.com/originals/81/f0/7a/81f07aed8e1f9d01d58e42743d046c19.jpg",
                location = "Kribi, Cameroon"
            )
        )

        // Initialize the tag selector
        val tagSelector = TagSelector()

        // Set up the adapter with tagSelector
        val adapter = PlaceAdapter(this, places, tagSelector)
        recyclerView.adapter = adapter

        // Later, when needed, you can get the selected places like this:
        // val selectedPlaces = tagSelector.getSelectedPlaces()
    }
}
