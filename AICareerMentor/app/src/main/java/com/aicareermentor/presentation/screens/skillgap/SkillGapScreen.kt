package com.aicareermentor.presentation.screens.skillgap

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicareermentor.domain.model.RoadmapPhase
import com.aicareermentor.domain.model.SkillGapAnalysis
import com.aicareermentor.domain.model.popularRoles
import com.aicareermentor.presentation.components.*
import com.aicareermentor.presentation.screens.UiState
import com.aicareermentor.presentation.theme.*

@Composable
fun SkillGapScreen(
    onBack: () -> Unit,
    viewModel: SkillGapViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.onPdfSelected(it) }
    }

    Scaffold(topBar = { AppTopBar("Skill Gap Detector", onBack = onBack) }) { pad ->
        Column(
            modifier            = Modifier.fillMaxSize().padding(pad)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Optional resume upload
            OutlinedButton(onClick = { launcher.launch("application/pdf") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.FileUpload, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (state.resumeFileName.isEmpty()) "Upload Resume (Optional — improves accuracy)" else "✅ ${state.resumeFileName}")
            }

            Text("Select Target Role", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(popularRoles) { role ->
                    FilterChip(
                        selected = state.selectedRole == role.title,
                        onClick  = { viewModel.onRoleSelected(role.title) },
                        label    = { Text("${role.icon} ${role.title}") },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            AnimatedVisibility(visible = state.selectedRole.isNotEmpty(), enter = fadeIn() + expandVertically()) {
                GradientButton(
                    text    = "🔍  Detect Skill Gap",
                    onClick = { viewModel.analyze() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.analysisState !is UiState.Loading,
                    colors  = blueGradient
                )
            }

            when (val s = state.analysisState) {
                is UiState.Loading -> LoadingState("Detecting skill gaps…")
                is UiState.Error   -> ErrorState(s.message) { viewModel.analyze() }
                is UiState.Success -> SkillGapResultUI(s.data)
                is UiState.Idle    -> {
                    if (state.selectedRole.isEmpty())
                        InfoCard("🎯", "Choose a Role", "Pick a target role from the list above. We'll compare it against your profile and identify exactly what you need to learn.")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SkillGapResultUI(analysis: SkillGapAnalysis) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Match score card
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ScoreCircle(score = analysis.matchPercentage, label = "Role Match", size = 130.dp)
                Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primaryContainer) {
                    Text("Ready in ${analysis.estimatedTimeToReady}",
                        style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
                }
            }
        }

        // Skills comparison
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (analysis.currentSkills.isNotEmpty()) {
                    SectionHeader("✅ Your Current Skills")
                    ChipRow(items = analysis.currentSkills, chipColor = Color(0xFF22C55E).copy(0.15f))
                }
                if (analysis.missingSkills.isNotEmpty()) {
                    SectionHeader("❌ Missing Skills")
                    ChipRow(items = analysis.missingSkills, chipColor = MaterialTheme.colorScheme.errorContainer)
                }
            }
        }

        // Roadmap
        if (analysis.roadmap.isNotEmpty()) {
            Text("📍 Learning Roadmap", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            analysis.roadmap.forEachIndexed { index, phase ->
                RoadmapPhaseCard(phase, index)
            }
        }

        // Projects
        if (analysis.recommendedProjects.isNotEmpty()) {
            Text("🚀 Recommended Projects", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            analysis.recommendedProjects.forEach { project ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(project.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(project.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (project.skills.isNotEmpty())
                            ChipRow(items = project.skills, chipColor = MaterialTheme.colorScheme.secondaryContainer)
                    }
                }
            }
        }
    }
}

@Composable
private fun RoadmapPhaseCard(phase: RoadmapPhase, index: Int) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.5f))) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primary) {
                    Text("${index + 1}", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge)
                }
                Column {
                    Text(phase.phase, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text("⏱ ${phase.duration}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (phase.topics.isNotEmpty())
                ChipRow(items = phase.topics, chipColor = MaterialTheme.colorScheme.primaryContainer)
            if (phase.resources.isNotEmpty()) {
                Text("Resources:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
                phase.resources.take(3).forEach { BulletItem(it, color = MaterialTheme.colorScheme.primary) }
            }
        }
    }
}
