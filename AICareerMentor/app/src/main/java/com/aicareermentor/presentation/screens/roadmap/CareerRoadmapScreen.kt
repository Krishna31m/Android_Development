package com.aicareermentor.presentation.screens.roadmap

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.aicareermentor.domain.model.CareerRoadmap
import com.aicareermentor.domain.model.RoadmapPhase
import com.aicareermentor.domain.model.popularRoles
import com.aicareermentor.presentation.components.*
import com.aicareermentor.presentation.screens.UiState
import com.aicareermentor.presentation.theme.*

@Composable
fun CareerRoadmapScreen(
    onBack: () -> Unit,
    viewModel: CareerRoadmapViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(topBar = { AppTopBar("Career Roadmap", onBack = onBack) }) { pad ->
        Column(
            modifier            = Modifier.fillMaxSize().padding(pad)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Select Domain", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(popularRoles) { role ->
                    FilterChip(
                        selected = state.selectedDomain == role.title,
                        onClick  = { viewModel.onDomainSelected(role.title) },
                        label    = { Text("${role.icon} ${role.title}") },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            Text("Current Level", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                viewModel.levels.forEach { level ->
                    FilterChip(
                        selected = state.selectedLevel == level,
                        onClick  = { viewModel.onLevelSelected(level) },
                        label    = { Text(level) },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }

            AnimatedVisibility(visible = state.selectedDomain.isNotEmpty(), enter = fadeIn() + expandVertically()) {
                GradientButton(
                    text    = "🗺️  Generate Roadmap",
                    onClick = { viewModel.generateRoadmap() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.roadmapState !is UiState.Loading,
                    colors  = amberGradient
                )
            }

            when (val s = state.roadmapState) {
                is UiState.Loading -> LoadingState("Building your career roadmap…")
                is UiState.Error   -> ErrorState(s.message) { viewModel.generateRoadmap() }
                is UiState.Success -> RoadmapResultUI(s.data)
                is UiState.Idle    -> {
                    if (state.selectedDomain.isEmpty())
                        InfoCard("🗺️", "Plan Your Career Path", "Select a domain and your current level. AI will build a complete Beginner → Advanced roadmap with resources, projects and milestones.")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RoadmapResultUI(roadmap: CareerRoadmap) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Summary card
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(amberGradient))
            .padding(22.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(roadmap.domain, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (roadmap.totalDuration.isNotEmpty())
                        Text("⏱ ${roadmap.totalDuration}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(0.9f))
                    if (roadmap.salaryRange.isNotEmpty())
                        Text("💰 ${roadmap.salaryRange}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(0.9f))
                }
                Text("${roadmap.phases.size} learning phases", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.75f))
            }
        }

        // Phases
        Text("Learning Phases", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        roadmap.phases.forEachIndexed { idx, phase ->
            PhaseCard(phase, idx)
        }

        // Career paths
        if (roadmap.careerPaths.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeader("🎯 Career Paths After Completion")
                    ChipRow(items = roadmap.careerPaths, chipColor = MaterialTheme.colorScheme.primaryContainer)
                }
            }
        }

        // Top companies
        if (roadmap.topCompanies.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeader("🏢 Top Hiring Companies")
                    ChipRow(items = roadmap.topCompanies, chipColor = MaterialTheme.colorScheme.secondaryContainer)
                }
            }
        }
    }
}

@Composable
private fun PhaseCard(phase: RoadmapPhase, index: Int) {
    val levelColor = when (phase.level.lowercase()) {
        "beginner"     -> Color(0xFF22C55E)
        "intermediate" -> ScoreAmber
        "advanced"     -> ScoreRed
        else           -> GradientStart
    }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(10.dp), color = levelColor) {
                    Text("${index + 1}", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.White, fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleSmall)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(phase.phase, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (phase.level.isNotEmpty()) Text("📊 ${phase.level}", style = MaterialTheme.typography.bodySmall, color = levelColor)
                        Text("⏱ ${phase.duration}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            if (phase.objectives.isNotEmpty()) {
                Text("Objectives:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                phase.objectives.take(3).forEach { BulletItem(it) }
            }

            if (phase.topics.isNotEmpty()) {
                Text("Skills to Learn:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                ChipRow(items = phase.topics, chipColor = MaterialTheme.colorScheme.primaryContainer)
            }

            if (phase.projects.isNotEmpty()) {
                Text("Practice Projects:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                phase.projects.take(3).forEach { BulletItem(it, color = GradientStart) }
            }

            if (phase.milestones.isNotEmpty()) {
                Text("Milestones:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                phase.milestones.take(3).forEach { milestone ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("✅", fontSize = 14.sp)
                        Text(milestone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            if (phase.resources.isNotEmpty()) {
                Text("📚 Resources:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                phase.resources.take(3).forEach { BulletItem(it, color = MaterialTheme.colorScheme.primary) }
            }
        }
    }
}
