package com.gurukiran.mcqquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gurukiran.mcqquiz.ui.nav.QuizNavGraph
import com.gurukiran.mcqquiz.ui.theme.McqTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            McqTheme {
                QuizNavGraph()
            }
        }
    }
}
