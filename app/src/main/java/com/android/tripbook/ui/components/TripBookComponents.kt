package com.android.tripbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.ui.theme.TripBookColors

/**
 * Standardized gradient background for all screens
 */
@Composable
fun TripBookGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        TripBookColors.GradientStart,
                        TripBookColors.GradientEnd
                    )
                )
            )
    ) {
        content()
    }
}

/**
 * Standardized header with back button and title
 */
@Composable
fun TripBookHeader(
    title: String,
    subtitle: String? = null,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = TripBookColors.TextOnPrimary.copy(alpha = 0.2f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TripBookColors.TextOnPrimary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TripBookColors.TextOnPrimary
                )
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = TripBookColors.TextOnPrimary.copy(alpha = 0.9f)
                    )
                )
            }
        }
    }
}

/**
 * Standardized content card with rounded corners
 */
@Composable
fun TripBookContentCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        colors = CardDefaults.cardColors(containerColor = TripBookColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            content = content
        )
    }
}

/**
 * Standardized text field with consistent styling
 */
@Composable
fun TripBookTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TripBookColors.TextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            leadingIcon = leadingIcon,
            isError = isError,
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            supportingText = {
                if (isError && errorMessage != null) {
                    Text(errorMessage, color = MaterialTheme.colorScheme.error)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TripBookColors.BorderFocused,
                focusedLabelColor = TripBookColors.BorderFocused,
                unfocusedBorderColor = TripBookColors.Border,
                disabledBorderColor = if (isError) MaterialTheme.colorScheme.error else TripBookColors.Border,
                disabledTextColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

/**
 * Standardized primary button
 */
@Composable
fun TripBookPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = TripBookColors.Primary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TripBookColors.TextOnPrimary
        )
    }
}

/**
 * Standardized secondary button
 */
@Composable
fun TripBookSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = TripBookColors.Secondary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TripBookColors.TextOnPrimary
        )
    }
}

/**
 * Standardized filter chip
 */
@Composable
fun TripBookFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TripBookColors.Primary,
            selectedLabelColor = TripBookColors.TextOnPrimary,
            containerColor = TripBookColors.SurfaceVariant,
            labelColor = TripBookColors.TextSecondary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = if (selected) TripBookColors.Primary else TripBookColors.Divider,
            selectedBorderColor = TripBookColors.Primary
        ),
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Standardized section title
 */
@Composable
fun TripBookSectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = TripBookColors.TextPrimary,
        modifier = modifier.padding(bottom = 12.dp)
    )
}
