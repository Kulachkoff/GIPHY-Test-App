package com.chililabs.giphytest.utils.ext

import java.util.Locale.getDefault

fun String.capitalize(): String =
    this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(getDefault())
        else it.toString()
    }

fun String.stripApiKey(): String =
    this.replace(Regex("""(?i)api_key=[^&\s]+"""), "api_key=**REDACTED**")