package com.gurukiran.mcqquiz.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun McqTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        typography = Typography(),
        content = content
    )
}
