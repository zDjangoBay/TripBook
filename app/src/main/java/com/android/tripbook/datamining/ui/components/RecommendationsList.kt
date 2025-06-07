package com.android.tripbook.datamining.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.tripbook.datamining.data.feedback.RecommendationFeedback.FeedbackType
import com.android.tripbook.datamining.data.model.TravelRecommendation

/**
 * A list of travel recommendations with feedback controls
 */
@Composable
fun RecommendationsList(
    recommendations: List<TravelRecommendation>,
    isLoading: Boolean,
    onRecommendationClick: (TravelRecommendation) -> Unit,
    onFeedback: (TravelRecommendation, FeedbackType, Float?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (recommendations.isEmpty()) {
            Text(
                text = "No recommendations found",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = recommendations,
                    key = { it.id }
                ) { recommendation ->
                    RecommendationCard(
                        recommendation = recommendation,
                        onClick = onRecommendationClick,
                        onFeedback = onFeedback
                    )
                }
            }
        }
    }
}
