package com.example.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape,
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    leadingIcon: Painter
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholderText,
                style = MaterialTheme.typography.labelLarge
            )
        },
        textStyle = MaterialTheme.typography.labelLarge,
        singleLine = true,
        leadingIcon = {
            Icon(
                painter = leadingIcon,
                contentDescription = placeholderText,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}

object ShapeAppTextField {
    val TOP_ROUNDING = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
    val BOTTOM_ROUNDING = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
    val WITHOUT_ROUNDING = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp)
}