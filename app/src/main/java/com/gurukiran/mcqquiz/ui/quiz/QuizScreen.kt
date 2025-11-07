package com.gurukiran.mcqquiz.ui.quiz

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.gurukiran.mcqquiz.R
import com.gurukiran.mcqquiz.utils.activityViewModel
import com.gurukiran.mcqquiz.viewmodel.QuizViewModel

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun QuizScreen(onFinish: () -> Unit) {
    val vm: QuizViewModel = activityViewModel()
    val questions by vm.questions.collectAsState()
    val index by vm.currentIndex.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val selected by vm.selectedAnswer.collectAsState()
    val showAnswer by vm.showAnswer.collectAsState()
    val streak by vm.streak.collectAsState()
    val isOffline by vm.isOffline.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.observeInternet(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0A0F1F), Color(0xFF1A2738), Color(0xFF0A0F1F))
                )
            )
    ) {
        AnimatedVisibility(
            visible = isOffline,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.statusBars.asPaddingValues()),
                color = Color(0xFFFF9800),
                shadowElevation = 6.dp
            ) {
                Text(
                    text = "⚠ You're offline — Showing cached questions",
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0A0F1F), Color(0xFF1A2738), Color(0xFF0A0F1F))
                    )
                )
                .drawBehind {
                    drawCircle(
                        color = Color(0xFF6A85FF).copy(alpha = 0.05f),
                        radius = size.maxDimension / 1.3f,
                        center = center
                    )
                }
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(20.dp)
        ) {
            AnimatedContent(
                targetState = loading to error to questions.isEmpty() to (index >= questions.size),
                transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(400)) }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        loading -> Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFFFFC107),
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(42.dp)
                            )
                            Spacer(Modifier.height(14.dp))
                            Text("Getting your quiz ready...", color = Color.White.copy(0.8f))
                        }

                        error != null -> ErrorRetry(error!!, vm)

                        questions.isEmpty() -> Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "⚠ No Questions Found",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Please try again later", color = Color.Gray)
                        }

                        index >= questions.size -> LaunchedEffect(Unit) { onFinish() }

                        else -> QuizContent(vm, index, showAnswer, selected, streak)
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
private fun QuizContent(
    vm: QuizViewModel,
    index: Int,
    showAnswer: Boolean,
    selected: Int?,
    streak: Int
) {
    val questions by vm.questions.collectAsState()
    val q = questions[index]
    val progressAnim by animateFloatAsState(((index + 1) / questions.size.toFloat()), tween(700))

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Quiz Challenge 🚀", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, color = Color.White)

            Box(
                modifier = Modifier
                    .background(Color(0x30FFFFFF), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ){
                Text("🔥 $streak Streak", color = Color(0xFFFFC107), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(14.dp))

        LinearProgressIndicator(
            progress = progressAnim,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(10.dp)),
            color = Color(0xFFFFC107),
            trackColor = Color(0x33FFFFFF)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "Question ${index + 1}/${questions.size}",
            color = Color.LightGray,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(22.dp))

        AnimatedContent(targetState = index, transitionSpec = {
            slideInHorizontally { 300 } + fadeIn() togetherWith slideOutHorizontally { -300 } + fadeOut()
        }) {
            Text(
                q.question,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(26.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            items(q.options.size) { i ->

                val option = q.options[i]
                val isCorrect = q.answerIndex == i
                val isSelected = selected == i
                val animateScale = remember { Animatable(1f) }

                LaunchedEffect(isSelected) {
                    if (isSelected) animateScale.animateTo(1.05f, tween(150))
                    animateScale.animateTo(1f)
                }

                val bgColor = when {
                    showAnswer && isCorrect -> Color(0xFF4CAF50)
                    showAnswer && isSelected && !isCorrect -> Color(0xFFE53935)
                    else -> Color(0x22FFFFFF)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .scale(animateScale.value)
                        .clickable(enabled = !showAnswer) { vm.selectAnswer(i) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    border = if (isSelected) BorderStroke(1.5.dp, Color.White.copy(0.6f)) else null
                ) {
                    Row(
                        Modifier.fillMaxSize().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(option, color = Color.White, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        Spacer(Modifier.height(26.dp))

        OutlinedButton(
            onClick = { vm.skipQuestion() },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.5.dp, Color.White.copy(0.3f))
        ) {
            Text("Skip Question →", color = Color.White.copy(0.8f))
        }
        if (streak >= 3) SparkConfetti()
    }
}

@Composable
fun SparkConfetti() {
    val comp by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.confetti))
    val progress by animateLottieCompositionAsState(comp)
    LottieAnimation(
        comp, progress,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    )
}

@Composable
fun ErrorRetry(error: String, vm: QuizViewModel) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Something went wrong ❌", color = Color.Red, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(error, color = Color.LightGray)
        Spacer(Modifier.height(16.dp))
        Button(onClick = { vm.loadQuestions() }) { Text("Try Again") }
    }
}
