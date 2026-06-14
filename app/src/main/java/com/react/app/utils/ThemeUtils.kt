package com.react.app.utils

import android.content.Context

fun isDarkTheme(context: Context): Boolean {
    val config = context.resources.configuration
    return config.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES
}
