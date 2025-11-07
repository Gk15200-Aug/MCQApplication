package com.gurukiran.mcqquiz.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gurukiran.mcqquiz.ui.splash.SplashScreen
import com.gurukiran.mcqquiz.ui.quiz.QuizScreen
import com.gurukiran.mcqquiz.ui.result.ResultScreen

@Composable
fun QuizNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen {
                navController.navigate("quiz") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
        composable("quiz") {
            QuizScreen(onFinish = { navController.navigate("result") })
        }
        composable("result") {
            ResultScreen(onRestart = {
                navController.popBackStack("quiz", inclusive = false)
            })
        }
    }
}
