package com.android.tripbook.ui.components.travel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment // Importe Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.data.model.TravelLocation // Importe le modèle canonique
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
        horizontalAlignment = Alignment.CenterHorizontally //  utiliser Alignment.Horizontal
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



@Preview(showBackground = true)
@Composable
fun PreviewRouteMap() {
    TripBookTheme {
        // Utilise le modèle canonique TravelLocation (de data.model)
        val start = TravelLocation("1", "Paris", 48.85, 2.35, "Ville Lumière", null)
        val end = TravelLocation("2", "Lyon", 45.76, 4.83, "Capitale de la gastronomie", null)
        RouteMap(startLocation = start, endLocation = end) // Passe les deux paramètres attendus
    }
}
