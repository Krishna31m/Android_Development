package com.aicareermentor.presentation.screens.interview

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicareermentor.domain.model.AnswerEvaluation
import com.aicareermentor.domain.model.InterviewQuestion
import com.aicareermentor.domain.model.popularRoles
import com.aicareermentor.presentation.components.*
import com.aicareermentor.presentation.screens.UiState
import com.aicareermentor.presentation.theme.*

@Composable
fun InterviewScreen(
    onBack: () -> Unit,
    viewModel: InterviewViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(topBar = { AppTopBar("Mock Interview", onBack = onBack) }) { pad ->
        Column(
            modifier            = Modifier.fillMaxSize().padding(pad)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                state.sessionComplete -> SessionSummary(
                    evaluations  = state.evaluations,
                    averageScore = viewModel.averageScore(),
                    role         = state.selectedRole,
                    onRestart    = { viewModel.restart() }
                )
                state.questionsState is UiState.Loading -> LoadingState("Generating your interview questions…")
                state.questionsState is UiState.Success -> {
                    val qs = (state.questionsState as UiState.Success).data
                    QuestionSession(
                        question        = qs[state.currentIndex],
                        number          = state.currentIndex + 1,
                        total           = qs.size,
                        answer          = state.currentAnswer,
                        onAnswerChanged = { viewModel.onAnswerChanged(it) },
                        onSubmit        = { viewModel.submitAnswer() },
                        evalState       = state.evaluationState,
                        onNext          = { viewModel.nextQuestion() }
                    )
                }
                else -> {
                    RoleSelectionUI(
                        selected   = state.selectedRole,
                        onSelect   = { viewModel.onRoleSelected(it) },
                        onStart    = { viewModel.generateQuestions() }
                    )
                    (state.questionsState as? UiState.Error)?.let {
                        ErrorState(it.message) { viewModel.generateQuestions() }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RoleSelectionUI(selected: String, onSelect: (String) -> Unit, onStart: () -> Unit) {
    // Hero banner
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
        .background(Brush.linearGradient(greenGradient))
        .padding(24.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("🎤", fontSize = 36.sp)
            Text("Mock Interview", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("10 AI-tailored questions • Real-time scoring • Expert feedback",
                style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.85f))
        }
    }

    Text("Choose Your Role", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(popularRoles) { role ->
            FilterChip(
                selected = selected == role.title,
                onClick  = { onSelect(role.title) },
                label    = { Text("${role.icon} ${role.title}") },
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }

    AnimatedVisibility(visible = selected.isNotEmpty(), enter = fadeIn() + expandVertically()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.5f))) {
                Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, null, tint = ScoreGreen, modifier = Modifier.size(20.dp))
                    Text("Role: $selected", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
            }
            GradientButton("🚀  Start Interview", onStart, Modifier.fillMaxWidth(), colors = greenGradient)
        }
    }
}

@Composable
private fun QuestionSession(
    question: InterviewQuestion,
    number: Int,
    total: Int,
    answer: String,
    onAnswerChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    evalState: UiState<AnswerEvaluation>,
    onNext: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Progress bar
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Question $number of $total", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                Text("${((number.toFloat() / total) * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, color = GradientStart)
            }
            LinearProgressIndicator(
                progress   = { number.toFloat() / total },
                modifier   = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color      = GradientStart,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        // Tags
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primaryContainer) {
                Text(question.category, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }
            DifficultyBadge(question.difficulty)
        }

        // Question card
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Q$number. ${question.question}", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold, lineHeight = 26.sp)
                if (question.hint.isNotEmpty()) {
                    Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                        Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Lightbulb, null, tint = ScoreAmber, modifier = Modifier.size(16.dp))
                            Text(question.hint, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Answer input
        OutlinedTextField(
            value         = answer,
            onValueChange = onAnswerChanged,
            modifier      = Modifier.fillMaxWidth().heightIn(min = 130.dp),
            placeholder   = { Text("Type your detailed answer here…") },
            shape         = RoundedCornerShape(16.dp),
            enabled       = evalState !is UiState.Loading && evalState !is UiState.Success,
            minLines      = 5
        )

        when (evalState) {
            is UiState.Loading -> LoadingState("AI is evaluating your answer…")
            is UiState.Success -> {
                EvalResultCard(evalState.data)
                GradientButton(
                    text    = if (number < total) "Next Question →" else "🎉 View Results",
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            is UiState.Error -> ErrorState(evalState.message) { onSubmit() }
            is UiState.Idle  -> GradientButton(
                text    = "Submit Answer",
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = answer.isNotBlank(),
                colors  = greenGradient
            )
        }
    }
}

@Composable
private fun EvalResultCard(eval: AnswerEvaluation) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.6f))) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ScoreCircle(score = eval.score, maxScore = 10, size = 84.dp)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(eval.verdict, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${eval.score}/10 points", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
            if (eval.strengths.isNotEmpty()) {
                SectionHeader("✅ What You Did Well")
                eval.strengths.forEach { BulletItem(it, ScoreGreen) }
            }
            if (eval.weaknesses.isNotEmpty()) {
                SectionHeader("⚠️ What Was Missing")
                eval.weaknesses.forEach { BulletItem(it, ScoreRed) }
            }
            SectionHeader("🎯 Ideal Answer Includes")
            Text(eval.idealAnswer, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
        }
    }
}

@Composable
private fun SessionSummary(
    evaluations: Map<Int, AnswerEvaluation>,
    averageScore: Float,
    role: String,
    onRestart: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(brandGradient))
            .padding(28.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("🎉", fontSize = 48.sp)
                Text("Interview Complete!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text(role, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(0.8f))
                ScoreCircle(score = (averageScore * 10).toInt(), label = "Avg Score", size = 120.dp)
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Question Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                evaluations.entries.sortedBy { it.key }.forEach { (idx, eval) ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Q${idx + 1}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            LinearProgressIndicator(
                                progress   = { eval.score / 10f },
                                modifier   = Modifier.width(90.dp).height(6.dp).clip(CircleShape),
                                color      = when { eval.score >= 7 -> ScoreGreen; eval.score >= 4 -> ScoreAmber; else -> ScoreRed }
                            )
                            Text("${eval.score}/10",
                                style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold,
                                color = when { eval.score >= 7 -> ScoreGreen; eval.score >= 4 -> ScoreAmber; else -> ScoreRed })
                        }
                    }
                }
            }
        }
        GradientButton("Start New Interview", onRestart, Modifier.fillMaxWidth())
    }
}
