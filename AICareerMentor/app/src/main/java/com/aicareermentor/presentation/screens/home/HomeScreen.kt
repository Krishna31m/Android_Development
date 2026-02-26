package com.aicareermentor.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicareermentor.presentation.components.*
import com.aicareermentor.presentation.theme.*
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onResume: () -> Unit,
    onSkillGap: () -> Unit,
    onInterview: () -> Unit,
    onRoadmap: () -> Unit,
    onHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        HeroHeader(onHistory = onHistory)
        Spacer(Modifier.height(24.dp))
        Column(
            modifier              = Modifier.padding(horizontal = 16.dp),
            verticalArrangement   = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.width(4.dp).height(22.dp).clip(CircleShape).background(GradientStart))
                Text("AI-Powered Tools", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))

            AnimatingCard(delayMs = 0L) {
                FeatureCard(
                    title       = "Resume Analyzer",
                    description = "Upload PDF → AI extracts skills, finds gaps, gives ATS score and improvement tips",
                    icon        = Icons.Default.Description,
                    iconColors  = listOf(Brand500, Violet500),
                    onClick     = onResume
                )
            }
            AnimatingCard(delayMs = 80L) {
                FeatureCard(
                    title       = "Skill Gap Detector",
                    description = "Select your dream role → AI maps your skills vs requirements and builds a roadmap",
                    icon        = Icons.Default.Analytics,
                    iconColors  = blueGradient,
                    onClick     = onSkillGap
                )
            }
            AnimatingCard(delayMs = 160L) {
                FeatureCard(
                    title       = "Mock Interview",
                    description = "Practice 10 AI-tailored questions, submit answers and receive scored feedback instantly",
                    icon        = Icons.Default.RecordVoiceOver,
                    iconColors  = greenGradient,
                    onClick     = onInterview
                )
            }
            AnimatingCard(delayMs = 240L) {
                FeatureCard(
                    title       = "Career Roadmap",
                    description = "Beginner → Advanced: full phase-by-phase plan with resources, projects and milestones",
                    icon        = Icons.Default.Map,
                    iconColors  = amberGradient,
                    onClick     = onRoadmap
                )
            }

            Spacer(Modifier.height(20.dp))
            StatsRow()
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AnimatingCard(delayMs: Long, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(delayMs); visible = true }
    AnimatedVisibility(
        visible  = visible,
        enter    = slideInVertically(initialOffsetY = { it / 3 }, animationSpec = tween(400, easing = EaseOutCubic)) + fadeIn(tween(400))
    ) { content() }
}

@Composable
private fun HeroHeader(onHistory: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(270.dp)) {
        // Gradient BG
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.linearGradient(
                    colors = listOf(Brand900, Brand600, Violet600),
                    start  = Offset(0f, 0f),
                    end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
        )
        // Decorative blobs
        Box(Modifier.size(220.dp).offset((-50).dp, (-70).dp).clip(CircleShape)
            .background(Color.White.copy(alpha = 0.05f)))
        Box(Modifier.size(160.dp).align(Alignment.BottomEnd).offset(50.dp, 50.dp).clip(CircleShape)
            .background(Pink500.copy(alpha = 0.18f)))
        Box(Modifier.size(100.dp).align(Alignment.CenterEnd).offset(30.dp, (-40).dp).clip(CircleShape)
            .background(Violet400.copy(alpha = 0.15f)))

        Column(
            modifier            = Modifier.fillMaxSize().padding(start = 24.dp, end = 16.dp, top = 60.dp, bottom = 22.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top row
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("🤖", fontSize = 26.sp)
                        Text("AI Career Mentor", style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                    Text("Your intelligent career growth partner",
                        style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(0.75f))
                }
                Box(
                    modifier = Modifier.shadow(4.dp, RoundedCornerShape(14.dp)).clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(0.15f))
                ) {
                    IconButton(onClick = onHistory) {
                        Icon(Icons.Default.History, contentDescription = "History", tint = Color.White)
                    }
                }
            }

            // Stat chips row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HeroChip("Resume AI", "✓ Active", ScoreGreen, Modifier.weight(1f))
                HeroChip("Interview", "✓ Ready", Color(0xFF34D399), Modifier.weight(1f))
                HeroChip("Roadmap", "✓ Ready", ScoreAmber, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun HeroChip(title: String, sub: String, accent: Color, modifier: Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(14.dp)).background(Color.White.copy(0.13f)).padding(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(sub,   style = MaterialTheme.typography.labelSmall,  color = accent,           fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.labelMedium, color = Color.White,       fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun StatsRow() {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.5f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceAround) {
            StatItem(Icons.Default.Psychology,    "AI Features",   "4")
            VerticalDivider(modifier = Modifier.height(40.dp))
            StatItem(Icons.Default.QuestionAnswer,"Interview Qs", "10")
            VerticalDivider(modifier = Modifier.height(40.dp))
            StatItem(Icons.Default.School,        "Domains",      "12+")
        }
    }
}

@Composable
private fun StatItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, tint = GradientStart, modifier = Modifier.size(22.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        Text(label, style = MaterialTheme.typography.labelSmall,  color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
