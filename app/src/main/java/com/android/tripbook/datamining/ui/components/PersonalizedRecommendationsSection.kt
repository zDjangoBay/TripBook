package com.android.tripbook.datamining.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.datamining.data.feedback.RecommendationFeedback.FeedbackType
import com.android.tripbook.datamining.data.model.TravelRecommendation

/**
 * Section displaying personalized travel recommendations
 */
@Composable
fun PersonalizedRecommendationsSection(
    recommendations: List<TravelRecommendation>,
    predictiveRecommendations: List<TravelRecommendation>,
    isLoading: Boolean,
    onRecommendationClick: (TravelRecommendation) -> Unit,
    onFeedback: (TravelRecommendation, FeedbackType, Float?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Section title
        Text(
            text = "Personalized Recommendations",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Section description
        Text(
            text = "Recommendations based on your preferences and travel history",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Standard recommendations
        RecommendationsList(
            recommendations = recommendations,
            isLoading = isLoading,
            onRecommendationClick = onRecommendationClick,
            onFeedback = onFeedback,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Predictive recommendations section
        if (predictiveRecommendations.isNotEmpty()) {
            Text(
                text = "Predictive Recommendations",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Recommendations based on current trends and seasonal patterns",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            RecommendationsList(
                recommendations = predictiveRecommendations,
                isLoading = isLoading,
                onRecommendationClick = onRecommendationClick,
                onFeedback = onFeedback,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
