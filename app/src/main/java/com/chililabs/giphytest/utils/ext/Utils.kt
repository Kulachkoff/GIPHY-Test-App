package com.chililabs.giphytest.utils.ext

import com.chililabs.giphytest.ui.navigation.Route
import com.chililabs.giphytest.ui.navigation.ScaffoldConfig
import java.util.Locale.getDefault

// Extension function to replace deprecated counterpart from kotlin.text
fun String.capitalize(): String =
    this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(getDefault())
        else it.toString()
    }

fun String.stripApiKey(): String =
    this.replace(Regex("""(?i)api_key=[^&\s]+"""), "api_key=**REDACTED**")

fun Route.hasTopBar(): Boolean =
    this.scaffoldConfig == ScaffoldConfig.FULL || this.scaffoldConfig == ScaffoldConfig.NO_BOTTOM_BAR

fun Route.hasBottomBar(): Boolean =
    this.scaffoldConfig == ScaffoldConfig.FULL || this.scaffoldConfig == ScaffoldConfig.NO_TOP_BAR