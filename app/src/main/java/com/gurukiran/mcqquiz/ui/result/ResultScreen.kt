package com.gurukiran.mcqquiz.ui.result

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.gurukiran.mcqquiz.R
import com.gurukiran.mcqquiz.utils.activityViewModel
import com.gurukiran.mcqquiz.viewmodel.QuizViewModel

@Composable
fun ResultScreen(
    onRestart: () -> Unit,
    vm: QuizViewModel = activityViewModel()
) {
    val score by vm.score.collectAsState()
    val questions by vm.questions.collectAsState()
    val longest by vm.sessionLongestStreak.collectAsState()

    // Counter animation for score and streak
    val animatedScore = animateIntAsState(targetValue = score, animationSpec = tween(1200))
    val animatedStreak = animateIntAsState(targetValue = longest, animationSpec = tween(1200))

    // Trophy Lottie animation
    val comp by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.confetti))
    val progress by animateLottieCompositionAsState(comp, iterations = 1)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A2A3A), Color(0xFF0D0E12))
                )
            )
            .padding(24.dp)
    ) {

        // Confetti on top
        LottieAnimation(comp, progress, modifier = Modifier.fillMaxWidth().height(180.dp))

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "🚀 Quiz Completed!",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFFFFC107)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Your brain just leveled up 🧠⚡",
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(20.dp))

            // Glassmorphic stat card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(20.dp))
                    .background(Color(0x1AFFFFFF), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x22FFFFFF))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "You've completed the quiz. Here's your performance summary.",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(22.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ResultStat(
                            title = "Correct",
                            value = "${animatedScore.value}/${questions.size}"
                        )
                        ResultStat(
                            title = "Top Streak",
                            value = animatedStreak.value.toString()
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = {
                    vm.restartQuiz()
                    onRestart()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
            ) {
                Text("Play Again 🚀", color = Color.Black, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun ResultStat(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = Color.White.copy(0.7f), style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFFFC107)
        )
    }
}
