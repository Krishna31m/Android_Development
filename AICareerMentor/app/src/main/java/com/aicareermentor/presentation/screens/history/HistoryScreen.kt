package com.aicareermentor.presentation.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicareermentor.domain.model.AnalysisHistory
import com.aicareermentor.domain.model.AnalysisType
import com.aicareermentor.presentation.components.AppTopBar
import com.aicareermentor.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val history by viewModel.history.collectAsStateWithLifecycle()

    Scaffold(topBar = { AppTopBar("History", onBack = onBack) }) { pad ->
        if (history.isEmpty()) {
            EmptyHistory(modifier = Modifier.fillMaxSize().padding(pad))
        } else {
            LazyColumn(
                modifier        = Modifier.fillMaxSize().padding(pad),
                contentPadding  = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(history, key = { it.id }) { item ->
                    HistoryCard(item = item, onDelete = { viewModel.delete(item.id) })
                }
            }
        }
    }
}

@Composable
private fun EmptyHistory(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("📋", fontSize = 56.sp)
            Text("No History Yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Your analyses will appear here once you start using AI features.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 40.dp))
        }
    }
}

@Composable
private fun HistoryCard(item: AnalysisHistory, onDelete: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }
    val fmt = remember { SimpleDateFormat("MMM dd, yyyy  hh:mm a", Locale.getDefault()) }

    val typeColor = when (item.type) {
        AnalysisType.RESUME    -> Brand500
        AnalysisType.SKILL_GAP -> Color(0xFF0EA5E9)
        AnalysisType.INTERVIEW -> Color(0xFF10B981)
        AnalysisType.ROADMAP   -> ScoreAmber
    }
    val typeIcon = when (item.type) {
        AnalysisType.RESUME    -> "📄"
        AnalysisType.SKILL_GAP -> "🔍"
        AnalysisType.INTERVIEW -> "🎤"
        AnalysisType.ROADMAP   -> "🗺️"
    }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
            Surface(shape = RoundedCornerShape(12.dp), color = typeColor.copy(alpha = 0.15f)) {
                Text(typeIcon, modifier = Modifier.padding(10.dp), fontSize = 20.sp)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(50), color = typeColor.copy(alpha = 0.12f)) {
                        Text(item.type.label, style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), color = typeColor, fontWeight = FontWeight.Bold)
                    }
                    item.score?.let {
                        Text("Score: $it", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold,
                            color = when { it >= 70 -> ScoreGreen; it >= 40 -> ScoreAmber; else -> ScoreRed })
                    }
                }
                Text(item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(item.summary, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(fmt.format(Date(item.timestamp)), style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline)
            }
            IconButton(onClick = { showConfirm = true }) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
            }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title            = { Text("Delete Entry") },
            text             = { Text("Are you sure you want to delete this analysis?") },
            confirmButton    = {
                TextButton(onClick = { onDelete(); showConfirm = false }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton    = { TextButton(onClick = { showConfirm = false }) { Text("Cancel") } }
        )
    }
}
