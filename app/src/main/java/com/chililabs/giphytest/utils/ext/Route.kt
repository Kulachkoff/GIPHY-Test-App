package com.chililabs.giphytest.utils.ext

import androidx.annotation.StringRes
import com.chililabs.giphytest.R
import com.chililabs.giphytest.ui.navigation.Route

@StringRes
fun Route.getTitleRes(): Int = when (this) {
    is Route.Main -> R.string.nav_main
    is Route.Main.Tab.Search -> R.string.nav_search
    is Route.Main.Tab.Favorites -> R.string.nav_favorites
    is Route.Main.Common.Details -> R.string.nav_details
    is Route.Main.Common.Settings -> R.string.nav_settings
}