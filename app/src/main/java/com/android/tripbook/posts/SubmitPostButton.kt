package com.android.tripbook.posts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.R // Make sure this import points to your project's R file

@Composable
fun SubmitPostButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        shape = MaterialTheme.shapes.medium,
        enabled = enabled
    ) {
        Text(
            text = stringResource(id = R.string.submit_post),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SubmitPostButtonPreview() {
    MaterialTheme {
        SubmitPostButton(
            onClick = { /* Do nothing in preview */ },
            enabled = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SubmitPostButtonDisabledPreview() {
    MaterialTheme {
        SubmitPostButton(
            onClick = { /* Do nothing in preview */ },
            enabled = false
        )
    }
}
