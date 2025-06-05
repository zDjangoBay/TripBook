package com.android.tripbook.ui.components.travel


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.data.model.TravelLocation
import com.android.tripbook.ui.theme.TripBookTheme

@Composable
fun RouteMap(startLocation: TravelLocation?, endLocation: TravelLocation?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Arrangement.End
    ) {
        Text("Carte de l'itinéraire (Intégration réelle de carte nécessaire)")
        if (startLocation != null) {
            Text("De: ${startLocation.name}")
        }
        if (endLocation != null) {
            Text("À: ${endLocation.name}")
        }
    }
}

fun Column(modifier: Modifier, verticalArrangement: Arrangement.HorizontalOrVertical, horizontalAlignment: Arrangement.Horizontal, content: @Composable ColumnScope.() -> Unit) {

}

@Preview(showBackground = true)
@Composable
fun PreviewRouteMap() {
    TripBookTheme {
        val start =
            com.android.tripbook.posts.model.TravelLocation("1", "Paris", 48.85, 2.35, null, null)
        val end =
            com.android.tripbook.posts.model.TravelLocation("2", "Lyon", 45.76, 4.83, null, null)
        RouteMap(start)
    }
}

fun RouteMap(startLocation: com.android.tripbook.posts.model.TravelLocation) {
    TODO("Not yet implemented")
}
