package com.aicareermentor.presentation.screens.resume

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicareermentor.domain.model.ResumeAnalysis
import com.aicareermentor.presentation.components.*
import com.aicareermentor.presentation.screens.UiState
import com.aicareermentor.presentation.theme.*

@Composable
fun ResumeAnalyzerScreen(
    onBack: () -> Unit,
    viewModel: ResumeAnalyzerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.onPdfSelected(it) }
    }

    Scaffold(topBar = { AppTopBar("Resume Analyzer", onBack = onBack) }) { pad ->
        Column(
            modifier            = Modifier.fillMaxSize().padding(pad)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UploadCard(
                fileName       = state.fileName,
                charCount      = state.extractedText.length,
                extractionError = state.extractionError,
                onUpload       = { launcher.launch("application/pdf") }
            )

            AnimatedVisibility(
                visible = state.extractedText.isNotEmpty() && state.analysisState !is UiState.Loading,
                enter   = fadeIn() + expandVertically()
            ) {
                GradientButton(
                    text    = "✨  Analyze Resume with AI",
                    onClick = { viewModel.analyzeResume() },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            when (val s = state.analysisState) {
                is UiState.Loading -> LoadingState("Analyzing your resume…")
                is UiState.Error   -> ErrorState(s.message) { viewModel.analyzeResume() }
                is UiState.Success -> ResumeResultUI(s.data)
                is UiState.Idle    -> HintCard()
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HintCard() {
    InfoCard(
        emoji = "💡",
        title = "How it works",
        body  = "Upload your PDF resume, then tap Analyze. Our AI will score it, find skill gaps, rate it for ATS systems, and suggest targeted improvements."
    )
}

@Composable
private fun UploadCard(
    fileName: String,
    charCount: Int,
    extractionError: String?,
    onUpload: () -> Unit
) {
    val hasFile = fileName.isNotEmpty()
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier.size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (hasFile) Brush.linearGradient(greenGradient)
                        else Brush.linearGradient(brandGradient)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (hasFile) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                    null, tint = Color.White, modifier = Modifier.size(36.dp)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    if (hasFile) fileName else "Upload Your Resume",
                    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold,
                    color = if (hasFile) ScoreGreen else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    if (hasFile) "$charCount characters extracted • Ready to analyze"
                    else "Supports PDF format",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            extractionError?.let {
                Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.errorContainer) {
                    Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            OutlinedButton(onClick = onUpload, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.FileUpload, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (!hasFile) "Select PDF" else "Change File")
            }
        }
    }
}

@Composable
private fun ResumeResultUI(analysis: ResumeAnalysis) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Scores
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Analysis Results", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ScoreCircle(score = analysis.overallScore, label = "Overall Score")
                    ScoreCircle(score = analysis.atsScore,     label = "ATS Score")
                }
            }
        }

        // Summary
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionHeader("📝 AI Summary")
                Text(analysis.summary, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp)
            }
        }

        if (analysis.strengths.isNotEmpty())
            ChipCard("💪 Strengths", analysis.strengths, Color(0xFF22C55E).copy(0.15f))

        if (analysis.skillGaps.isNotEmpty())
            ChipCard("🔧 Skill Gaps", analysis.skillGaps, MaterialTheme.colorScheme.errorContainer)

        if (analysis.missingTechnologies.isNotEmpty())
            ChipCard("⚡ Missing Technologies", analysis.missingTechnologies, MaterialTheme.colorScheme.tertiaryContainer)

        if (analysis.improvements.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeader("💡 Improvement Suggestions")
                    analysis.improvements.forEach { BulletItem(it) }
                }
            }
        }

        if (analysis.keywordOptimization.isNotEmpty())
            ChipCard("🔑 Keywords to Add for ATS", analysis.keywordOptimization, MaterialTheme.colorScheme.secondaryContainer)
    }
}

@Composable
private fun ChipCard(title: String, items: List<String>, chipColor: Color) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(title)
            ChipRow(items = items, chipColor = chipColor)
        }
    }
}
