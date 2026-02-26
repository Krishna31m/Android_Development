package com.aicareermentor.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicareermentor.presentation.theme.*
import com.valentinilk.shimmer.shimmer

// ─────────────────────────────────────────────
//  Gradient helpers
// ─────────────────────────────────────────────

val brandGradient  get() = listOf(GradientStart, GradientMid, GradientEnd)
val greenGradient  = listOf(Color(0xFF10B981), Color(0xFF059669))
val blueGradient   = listOf(Color(0xFF0EA5E9), Color(0xFF0284C7))
val amberGradient  = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
val purpleGradient = listOf(Violet500, Violet600)

fun Brush.Companion.brand() = linearGradient(
    colors = listOf(GradientStart, GradientEnd),
    start  = Offset.Zero,
    end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)

// ─────────────────────────────────────────────
//  GradientButton
// ─────────────────────────────────────────────

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: List<Color> = brandGradient
) {
    val alpha by animateFloatAsState(if (enabled) 1f else 0.45f, label = "btn_alpha")
    Box(
        modifier = modifier
            .height(54.dp)
            .then(if (enabled) Modifier.shadow(6.dp, RoundedCornerShape(16.dp)) else Modifier)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = if (enabled) colors else listOf(Color.Gray, Color.Gray),
                    start  = Offset.Zero,
                    end    = Offset(Float.POSITIVE_INFINITY, 0f)
                )
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = text,
            style      = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color      = Color.White.copy(alpha = alpha),
            fontSize   = 15.sp
        )
    }
}

// ─────────────────────────────────────────────
//  GradientCard
// ─────────────────────────────────────────────

@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    colors: List<Color> = brandGradient,
    radius: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(radius))
            .clip(RoundedCornerShape(radius))
            .background(
                Brush.linearGradient(
                    colors = colors,
                    start  = Offset.Zero,
                    end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .padding(20.dp),
        content = content
    )
}

// ─────────────────────────────────────────────
//  FeatureCard
// ─────────────────────────────────────────────

@Composable
fun FeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dark = isSystemInDarkTheme()
    Card(
        modifier  = modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (dark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                             else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier  = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(4.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(iconColors, Offset.Zero, Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY))),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
            }
            Icon(Icons.Default.ChevronRight, null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp))
        }
    }
}

// ─────────────────────────────────────────────
//  ScoreCircle
// ─────────────────────────────────────────────

@Composable
fun ScoreCircle(
    score: Int,
    maxScore: Int = 100,
    size: Dp = 110.dp,
    label: String = ""
) {
    val animated by animateIntAsState(
        targetValue    = score,
        animationSpec  = tween(1200, easing = EaseOutCubic),
        label          = "score"
    )
    val color = when {
        score.toFloat() / maxScore >= 0.7f -> ScoreGreen
        score.toFloat() / maxScore >= 0.4f -> ScoreAmber
        else                               -> ScoreRed
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress     = { animated.toFloat() / maxScore },
                modifier     = Modifier.size(size),
                color        = color,
                trackColor   = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth  = 9.dp
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text       = "$animated",
                    style      = if (size >= 100.dp) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color      = color
                )
                if (maxScore != 100) {
                    Text("/ $maxScore", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        if (label.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ─────────────────────────────────────────────
//  ChipRow (FlowRow of chips)
// ─────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipRow(
    items: List<String>,
    modifier: Modifier = Modifier,
    chipColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    FlowRow(
        modifier             = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement  = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            Surface(shape = RoundedCornerShape(50), color = chipColor, tonalElevation = 1.dp) {
                Text(
                    text     = item,
                    style    = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color    = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  AppTopBar
// ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = actions,
        colors  = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

// ─────────────────────────────────────────────
//  Section header
// ─────────────────────────────────────────────

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text       = title,
        style      = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color      = MaterialTheme.colorScheme.primary,
        modifier   = modifier.padding(vertical = 2.dp)
    )
}

// ─────────────────────────────────────────────
//  BulletItem
// ─────────────────────────────────────────────

@Composable
fun BulletItem(text: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = color,
            modifier = Modifier.weight(1f), lineHeight = 22.sp)
    }
}

// ─────────────────────────────────────────────
//  LoadingState
// ─────────────────────────────────────────────

@Composable
fun LoadingState(message: String = "Analyzing with AI...") {
    val inf = rememberInfiniteTransition(label = "loader")
    val scale by inf.animateFloat(
        initialValue  = 0.85f,
        targetValue   = 1.1f,
        animationSpec = infiniteRepeatable(tween(900, easing = EaseInOut), RepeatMode.Reverse),
        label         = "scale"
    )
    Column(
        modifier              = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size((72 * scale).dp)
                .shadow((8 * scale).dp, CircleShape)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(GradientStart, GradientEnd))),
            contentAlignment = Alignment.Center
        ) { Text("🤖", fontSize = (32 * scale).sp) }

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(message, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            Text("Powered by Gemini AI", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        LinearProgressIndicator(
            modifier   = Modifier.fillMaxWidth(0.65f).height(5.dp).clip(CircleShape),
            color      = GradientStart,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

// ─────────────────────────────────────────────
//  ErrorState
// ─────────────────────────────────────────────

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier            = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("⚠️", fontSize = 48.sp)
        Text("Something went wrong", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(message, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        Button(onClick = onRetry, shape = RoundedCornerShape(12.dp)) { Text("Try Again") }
    }
}

// ─────────────────────────────────────────────
//  ShimmerCard
// ─────────────────────────────────────────────

@Composable
fun ShimmerCard(modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier.shimmer().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(5) { i ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (i % 3 == 0) 1f else if (i % 3 == 1) 0.75f else 0.9f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}

// ─────────────────────────────────────────────
//  InfoCard
// ─────────────────────────────────────────────

@Composable
fun InfoCard(
    emoji: String,
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(emoji, fontSize = 22.sp)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(body, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────
//  DifficultyBadge
// ─────────────────────────────────────────────

@Composable
fun DifficultyBadge(difficulty: String) {
    val (bg, text) = when (difficulty.lowercase()) {
        "hard"   -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        "medium" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        else     -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }
    Surface(shape = RoundedCornerShape(50), color = bg) {
        Text(difficulty, style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), color = text, fontWeight = FontWeight.Bold)
    }
}
