package com.smartutility.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartutility.ui.Screen
import com.smartutility.ui.theme.*
import kotlinx.coroutines.delay

// ── Data model for each tool card ────────────────────────────────────────────
data class ToolItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
    val tag: String,
    val route: String
)

@Composable
fun HomeScreen(navController: NavController) {

    val tools = listOf(
        ToolItem(
            title    = "Unit Converter",
            subtitle = "Length · Weight · Temperature · Speed",
            icon     = Icons.Default.Straighten,
            accent   = AccentCyan,
            tag      = "CONVERTER",
            route    = Screen.Converter.route
        ),
        ToolItem(
            title    = "Currency Exchange",
            subtitle = "Live rates · 30+ currencies",
            icon     = Icons.Default.CurrencyExchange,
            accent   = AccentMint,
            tag      = "LIVE",
            route    = Screen.Currency.route
        ),
        ToolItem(
            title    = "Stopwatch",
            subtitle = "Precision timing · Lap splits",
            icon     = Icons.Default.Timer,
            accent   = AccentAmber,
            tag      = "TIMER",
            route    = Screen.Stopwatch.route
        )
    )

    // Animate header in on first load
    var headerVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        headerVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Decorative background orb (top-right)
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = 100.dp, y = (-80).dp)
                .align(Alignment.TopEnd)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(AccentCyan.copy(alpha = 0.10f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(50)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // ── Header ────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = headerVisible,
                enter   = fadeIn(tween(600)) + slideInVertically(tween(600)) { -30 }
            ) {
                Column {
                    // Greeting based on time of day
                    val hour = java.util.Calendar.getInstance()
                        .get(java.util.Calendar.HOUR_OF_DAY)
                    val greeting = when {
                        hour < 12 -> "Good Morning"
                        hour < 17 -> "Good Afternoon"
                        else      -> "Good Evening"
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(AccentCyan, RoundedCornerShape(50))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text          = "SMART UTILITY",
                            style         = MaterialTheme.typography.labelLarge,
                            color         = AccentCyan,
                            letterSpacing = 3.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text       = "$greeting 👋",
                        style      = MaterialTheme.typography.titleMedium,
                        color      = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text       = "Your Pocket\nToolkit",
                        style      = MaterialTheme.typography.displaySmall,
                        color      = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 42.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text  = "Essential tools, beautifully crafted.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }

            // ── Section label ─────────────────────────────────────────────
            Text(
                text          = "TOOLS",
                style         = MaterialTheme.typography.labelSmall,
                color         = TextMuted,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── Tool Cards ────────────────────────────────────────────────
            tools.forEachIndexed { index, tool ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(200L + index * 120L)
                    visible = true
                }
                AnimatedVisibility(
                    visible = visible,
                    enter   = fadeIn(tween(500)) + slideInVertically(tween(500)) { 50 }
                ) {
                    ToolCard(
                        tool    = tool,
                        onClick = { navController.navigate(tool.route) }
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Footer ─────────────────────────────────────────────────────
            Divider(color = BorderNavy)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text     = "Smart Utility Toolkit  ·  v1.0",
                style    = MaterialTheme.typography.labelSmall,
                color    = TextMuted,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Individual Tool Card ──────────────────────────────────────────────────────
@Composable
fun ToolCard(tool: ToolItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = BorderStroke(1.dp, BorderNavy)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon box
            Box(
                modifier          = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(tool.accent.copy(alpha = 0.12f)),
                contentAlignment  = Alignment.Center
            ) {
                Icon(
                    imageVector    = tool.icon,
                    contentDescription = null,
                    tint           = tool.accent,
                    modifier       = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text       = tool.title,
                        style      = MaterialTheme.typography.titleMedium,
                        color      = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Tag badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(tool.accent.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text          = tool.tag,
                            style         = MaterialTheme.typography.labelSmall,
                            color         = tool.accent,
                            fontSize      = 9.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text  = tool.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Icon(
                imageVector        = Icons.Default.ChevronRight,
                contentDescription = null,
                tint               = TextMuted,
                modifier           = Modifier.size(20.dp)
            )
        }

        // Bottom accent line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(tool.accent.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
        )
    }
}