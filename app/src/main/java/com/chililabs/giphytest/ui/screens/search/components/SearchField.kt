package com.chililabs.giphytest.ui.screens.search.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    shape: Shape = RoundedCornerShape(8.dp),
    leadingIcon: ImageVector? = Icons.Default.Search,
    trailingIcon: ImageVector? = Icons.Default.Close
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        shape = shape,
        placeholder = { Text(placeholder) },
        singleLine = singleLine,
        leadingIcon = {
            leadingIcon?.let {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = "Search GIPHY"
                )
            }
        },
        trailingIcon = {
            if (value.isNotBlank()) {
                IconButton(
                    onClick = { onClear() },
                    content = {
                        trailingIcon?.let {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear Search"
                            )
                        }
                    }
                )
            }
        }
    )
}