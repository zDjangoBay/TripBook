package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import data.PasswordStrength

@Composable
fun PasswordStrengthIndicator(
    strength: PasswordStrength,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Strength Label and Score
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Password Strength:",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = getStrengthText(strength.score),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = getStrengthColor(strength.score)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(strength.score / 100f)
                    .clip(RoundedCornerShape(3.dp))
                    .background(getStrengthColor(strength.score))
            )
        }
        
        // Feedback
        if (strength.feedback.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Missing requirements:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    strength.feedback.forEach { feedback ->
                        Text(
                            text = "• $feedback",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getStrengthColor(score: Int): Color {
    return when {
        score < 25 -> Color(0xFFEF4444) // Red
        score < 50 -> Color(0xFFF59E0B) // Orange
        score < 75 -> Color(0xFFEAB308) // Yellow
        score < 100 -> Color(0xFF22C55E) // Light Green
        else -> Color(0xFF16A34A) // Green
    }
}

private fun getStrengthText(score: Int): String {
    return when {
        score < 25 -> "Very Weak"
        score < 50 -> "Weak"
        score < 75 -> "Good"
        score < 100 -> "Strong"
        else -> "Very Strong"
    }
}
