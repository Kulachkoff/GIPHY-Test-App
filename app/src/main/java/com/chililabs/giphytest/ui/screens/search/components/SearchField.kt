package com.chililabs.giphytest.ui.screens.search.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.chililabs.giphytest.R
import com.chililabs.giphytest.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    shape: Shape = RoundedCornerShape(Dimens.searchFieldCornerRadius),
    leadingIcon: ImageVector? = Icons.Default.Search,
    trailingIcon: ImageVector? = Icons.Default.Close,
    suggestions: List<String> = emptyList(),
    onSuggestionSelected: (String) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    var expanded by remember(isFocused, suggestions) {
        mutableStateOf(isFocused && suggestions.isNotEmpty())
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            modifier = modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryEditable,
                    enabled = true
                ),
            value = value,
            onValueChange = onValueChange,
            interactionSource = interactionSource,
            shape = shape,
            placeholder = { Text(text = placeholder) },
            singleLine = singleLine,
            trailingIcon = {
                if (value.isNotBlank()) {
                    IconButton(
                        onClick = { onClear() },
                        content = {
                            trailingIcon?.let {
                                Icon(
                                    imageVector = it,
                                    contentDescription = stringResource(R.string.cd_clear_search)
                                )
                            }
                        }
                    )
                }
            },
            leadingIcon = {
                leadingIcon?.let {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = stringResource(R.string.cd_search)
                    )
                }
            }
        )

        if (suggestions.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                suggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        text = { Text(text = suggestion) },
                        onClick = {
                            onSuggestionSelected(suggestion)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}