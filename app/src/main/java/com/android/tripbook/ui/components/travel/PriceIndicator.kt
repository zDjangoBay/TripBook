package com.android.tripbook.ui.components.travel


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.theme.TripBookTheme

@Composable
fun PriceIndicator(price: Any, currency: String = "€") {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "Prix: ${FormatUtils.formatPrice(price, currency)}", style = MaterialTheme.typography.headlineSmall)
    }
}

class FormatUtils {
    companion object {
        fun formatPrice(any: Any, currency: String) {

        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewPriceIndicator() {
    TripBookTheme {
        PriceIndicator(price = 125.50)
    }
}