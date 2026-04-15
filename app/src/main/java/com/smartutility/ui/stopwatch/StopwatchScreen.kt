package com.smartutility.ui.stopwatch

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.smartutility.ui.theme.*
import com.smartutility.viewmodel.LapEntry
import com.smartutility.viewmodel.StopwatchViewModel

@Composable
fun StopwatchScreen(
    navController: NavController,
    vm: StopwatchViewModel = viewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Background orb
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-60).dp, y = 200.dp)
                .background(
                    Brush.radialGradient(
                        listOf(AccentAmber.copy(alpha = 0.07f), Color.Transparent)
                    ),
                    RoundedCornerShape(50)
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ──────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        "Stopwatch",
                        style      = MaterialTheme.typography.headlineMedium,
                        color      = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Precision timing with lap splits",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            // ── Clock Face ───────────────────────────────────────────────
            Box(
                modifier         = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                ClockFace(
                    elapsedMs  = state.elapsedMs,
                    isRunning  = state.isRunning,
                    lapStartMs = state.lapStartMs
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Control Buttons ──────────────────────────────────────────
            Row(
                modifier             = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment    = Alignment.CenterVertically
            ) {
                // Reset / Lap button (left)
                if (state.elapsedMs > 0L) {
                    if (state.isRunning) {
                        // LAP button
                        SecondaryControlButton(
                            icon    = Icons.Default.Flag,
                            label   = "LAP",
                            color   = AccentCyan,
                            onClick = { vm.lap() }
                        )
                    } else {
                        // RESET button
                        SecondaryControlButton(
                            icon    = Icons.Default.Refresh,
                            label   = "RESET",
                            color   = ErrorColor,
                            onClick = { vm.reset() }
                        )
                    }
                } else {
                    // Placeholder to keep layout balanced
                    Spacer(modifier = Modifier.size(72.dp))
                }

                // Main Start/Pause button (center)
                MainControlButton(
                    isRunning = state.isRunning,
                    onStart   = { vm.start() },
                    onPause   = { vm.pause() }
                )

                // Placeholder right side (keeps center button centered)
                Spacer(modifier = Modifier.size(72.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Lap List ─────────────────────────────────────────────────
            if (state.laps.isNotEmpty()) {
                Divider(color = BorderNavy)
                Spacer(modifier = Modifier.height(8.dp))

                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp)
                ) {
                    Text(
                        "LAP",
                        style         = MaterialTheme.typography.labelMedium,
                        color         = TextMuted,
                        letterSpacing = 1.sp,
                        modifier      = Modifier.weight(1f)
                    )
                    Text(
                        "LAP TIME",
                        style         = MaterialTheme.typography.labelMedium,
                        color         = TextMuted,
                        letterSpacing = 1.sp,
                        modifier      = Modifier.weight(1f),
                        textAlign     = TextAlign.Center
                    )
                    Text(
                        "TOTAL",
                        style         = MaterialTheme.typography.labelMedium,
                        color         = TextMuted,
                        letterSpacing = 1.sp,
                        modifier      = Modifier.weight(1f),
                        textAlign     = TextAlign.End
                    )
                }

                LazyColumn(
                    modifier            = Modifier.fillMaxWidth(),
                    contentPadding      = PaddingValues(
                        horizontal = 24.dp,
                        vertical   = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.laps) { lap ->
                        LapRow(
                            lap         = lap,
                            isLatestLap = lap == state.laps.first()
                        )
                    }
                }
            }
        }
    }
}

// ── Clock Face ────────────────────────────────────────────────────────────────
@Composable
fun ClockFace(elapsedMs: Long, isRunning: Boolean, lapStartMs: Long) {
    val hours   = (elapsedMs / 3_600_000).toInt()
    val minutes = ((elapsedMs % 3_600_000) / 60_000).toInt()
    val seconds = ((elapsedMs % 60_000) / 1_000).toInt()
    val millis  = ((elapsedMs % 1_000) / 10).toInt()

    // Arc progress (0f–1f based on current minute's seconds)
    val arcProgress = (elapsedMs % 60_000) / 60_000f

    // Lap arc progress
    val lapElapsed    = elapsedMs - lapStartMs
    val lapArcProgress = (lapElapsed % 60_000) / 60_000f

    Box(
        modifier         = Modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer ring
        Canvas(modifier = Modifier.size(240.dp)) {
            val stroke = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            val inset  = 3.dp.toPx()

            // Track
            drawArc(
                color      = BorderNavy,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter  = false,
                style      = stroke,
                topLeft    = Offset(inset, inset),
                size       = size.copy(
                    width  = size.width - inset * 2,
                    height = size.height - inset * 2
                )
            )
            // Progress arc
            drawArc(
                brush      = Brush.sweepGradient(
                    listOf(AccentAmber.copy(alpha = 0f), AccentAmber)
                ),
                startAngle = -90f,
                sweepAngle = arcProgress * 360f,
                useCenter  = false,
                style      = stroke,
                topLeft    = Offset(inset, inset),
                size       = size.copy(
                    width  = size.width - inset * 2,
                    height = size.height - inset * 2
                )
            )
        }

        // Time display
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (hours > 0) {
                Text(
                    text      = "%02d".format(hours),
                    style     = MaterialTheme.typography.headlineSmall,
                    color     = TextSecondary,
                    fontWeight = FontWeight.Light
                )
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text      = "%02d:%02d".format(minutes, seconds),
                    style     = MaterialTheme.typography.displayMedium,
                    color     = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text      = ".%02d".format(millis),
                    style     = MaterialTheme.typography.headlineSmall,
                    color     = if (isRunning) AccentAmber else TextSecondary,
                    fontWeight = FontWeight.Bold,
                    modifier  = Modifier.padding(bottom = 6.dp)
                )
            }
            Text(
                text  = if (isRunning) "RUNNING" else if (elapsedMs > 0) "PAUSED" else "READY",
                style = MaterialTheme.typography.labelMedium,
                color = when {
                    isRunning    -> AccentAmber
                    elapsedMs > 0 -> AccentCyan
                    else          -> TextMuted
                },
                letterSpacing = 2.sp
            )
        }
    }
}

// ── Main Start/Pause Button ───────────────────────────────────────────────────
@Composable
fun MainControlButton(
    isRunning: Boolean,
    onStart  : () -> Unit,
    onPause  : () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(
                if (isRunning) AccentAmber.copy(alpha = 0.15f)
                else AccentMint.copy(alpha = 0.15f)
            )
            .border(
                2.dp,
                if (isRunning) AccentAmber else AccentMint,
                CircleShape
            )
            .clickable { if (isRunning) onPause() else onStart() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isRunning) Icons.Default.Pause
            else Icons.Default.PlayArrow,
            contentDescription = if (isRunning) "Pause" else "Start",
            tint     = if (isRunning) AccentAmber else AccentMint,
            modifier = Modifier.size(36.dp)
        )
    }
}

// ── Secondary Control Button (Lap / Reset) ────────────────────────────────────
@Composable
fun SecondaryControlButton(
    icon   : androidx.compose.ui.graphics.vector.ImageVector,
    label  : String,
    color  : Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.10f))
                .border(1.dp, color.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = label,
                tint               = color,
                modifier           = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text          = label,
            style         = MaterialTheme.typography.labelSmall,
            color         = color,
            letterSpacing = 1.sp
        )
    }
}

// ── Lap Row ───────────────────────────────────────────────────────────────────
@Composable
fun LapRow(lap: LapEntry, isLatestLap: Boolean) {
    val accentColor = if (isLatestLap) AccentAmber else TextSecondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isLatestLap) AccentAmber.copy(alpha = 0.07f) else CardNavy
            )
            .border(
                1.dp,
                if (isLatestLap) AccentAmber.copy(alpha = 0.3f) else BorderNavy,
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lap number
        Text(
            text      = "Lap %02d".format(lap.number),
            style     = MaterialTheme.typography.titleSmall,
            color     = accentColor,
            fontWeight = FontWeight.SemiBold,
            modifier  = Modifier.weight(1f)
        )
        // Lap time
        Text(
            text      = formatTime(lap.lapTime),
            style     = MaterialTheme.typography.titleSmall,
            color     = TextPrimary,
            fontWeight = FontWeight.Medium,
            modifier  = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        // Total time
        Text(
            text      = formatTime(lap.totalTime),
            style     = MaterialTheme.typography.bodyMedium,
            color     = TextSecondary,
            modifier  = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

// ── Format ms → mm:ss.mm ─────────────────────────────────────────────────────
fun formatTime(ms: Long): String {
    val minutes = (ms / 60_000).toInt()
    val seconds = ((ms % 60_000) / 1_000).toInt()
    val millis  = ((ms % 1_000) / 10).toInt()
    return "%02d:%02d.%02d".format(minutes, seconds, millis)
}

// Color for reset button
val ErrorColor = Color(0xFFFF4D6A)