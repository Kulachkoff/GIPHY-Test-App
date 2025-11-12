package com.chililabs.giphytest.ui.main.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chililabs.giphytest.R
import com.chililabs.giphytest.ui.navigation.Route
import com.chililabs.giphytest.utils.ext.getTitleRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentRoute: Route,
    onBackClick: () -> Unit,
    title: String? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title ?: stringResource(currentRoute.getTitleRes())
            )
        },
        navigationIcon = {
            if (currentRoute.hasNavigationButton) {
                IconButton(
                    onClick = onBackClick,
                    content = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.cd_go_back)
                        )
                    }
                )
            }
        },
        actions = actions
    )
}