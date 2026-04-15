package com.smartutility.ui.currency

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.smartutility.ui.theme.*
import com.smartutility.viewmodel.CurrencyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyScreen(
    navController: NavController,
    vm: CurrencyViewModel = viewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val currencies = vm.allCurrencies

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Background orb
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = 100.dp, y = (-60).dp)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        listOf(AccentMint.copy(alpha = 0.08f), Color.Transparent)
                    ),
                    RoundedCornerShape(50)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ── Top Bar ──────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                        tint = TextSecondary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Currency Exchange",
                        style      = MaterialTheme.typography.headlineMedium,
                        color      = TextPrimary,
                        fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    if (state.isOffline) AccentAmber else AccentMint,
                                    RoundedCornerShape(50)
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            state.lastUpdated.ifEmpty { "Loading..." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (state.isOffline) AccentAmber else TextSecondary
                        )
                    }
                }
                // Refresh button
                IconButton(
                    onClick  = { vm.fetchRates() },
                    enabled  = !state.isLoading
                ) {
                    Icon(
                        imageVector        = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint               = if (state.isLoading) TextMuted else AccentMint
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Offline Banner ───────────────────────────────────────────
            AnimatedVisibility(visible = state.isOffline) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentAmber.copy(alpha = 0.12f))
                        .border(1.dp, AccentAmber.copy(alpha = 0.3f),
                            RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WifiOff, contentDescription = null,
                            tint     = AccentAmber,
                            modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Using offline rates. Tap refresh for live data.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AccentAmber)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Exchange Card ────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape  = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                border = BorderStroke(1.dp, BorderNavy)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // FROM
                    Text("FROM", style = MaterialTheme.typography.labelMedium,
                        color = TextMuted, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    CurrencyDropdown(
                        currencies  = currencies,
                        selected    = state.fromCurrency,
                        accentColor = AccentMint,
                        onSelect    = { vm.setFromCurrency(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value         = state.inputAmount,
                        onValueChange = { vm.onInputChange(it) },
                        modifier      = Modifier.fillMaxWidth(),
                        placeholder   = { Text("Amount", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        singleLine = true,
                        shape      = RoundedCornerShape(12.dp),
                        colors     = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = AccentMint,
                            unfocusedBorderColor = BorderNavy,
                            focusedTextColor     = TextPrimary,
                            unfocusedTextColor   = TextPrimary,
                            cursorColor          = AccentMint
                        ),
                        textStyle = MaterialTheme.typography.headlineMedium
                    )

                    // Swap
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier         = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        FilledTonalButton(
                            onClick = { vm.swapCurrencies() },
                            shape   = RoundedCornerShape(12.dp),
                            colors  = ButtonDefaults.filledTonalButtonColors(
                                containerColor = BorderNavy,
                                contentColor   = TextSecondary
                            )
                        ) {
                            Icon(Icons.Default.SwapVert, contentDescription = "Swap",
                                modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SWAP", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // TO
                    Text("TO", style = MaterialTheme.typography.labelMedium,
                        color = TextMuted, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    CurrencyDropdown(
                        currencies  = currencies,
                        selected    = state.toCurrency,
                        accentColor = AccentMint,
                        onSelect    = { vm.setToCurrency(it) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Result
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(AccentMint.copy(alpha = 0.08f))
                            .border(1.dp, AccentMint.copy(alpha = 0.25f),
                                RoundedCornerShape(16.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Text("RESULT",
                                style         = MaterialTheme.typography.labelMedium,
                                color         = AccentMint,
                                letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            if (state.result.isNotEmpty()) {
                                Text(
                                    "${state.result} ${state.toCurrency}",
                                    style      = MaterialTheme.typography.displaySmall,
                                    color      = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "${state.inputAmount} ${state.fromCurrency}" +
                                            " = ${state.result} ${state.toCurrency}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            } else {
                                Text("— —",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = TextMuted)
                            }
                        }
                    }
                }
            }

            // ── Popular Rates Grid ───────────────────────────────────────
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "POPULAR CURRENCIES",
                style         = MaterialTheme.typography.labelSmall,
                color         = TextMuted,
                letterSpacing = 2.sp,
                modifier      = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            val popular   = listOf("USD","EUR","GBP","KES","JPY","NGN","INR","AUD")
            val inputAmt  = state.inputAmount.toDoubleOrNull() ?: 1.0
            val fromRate  = state.rates[state.fromCurrency] ?: 1.0

            popular.filter { it != state.fromCurrency }
                .chunked(2)
                .forEach { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        pair.forEach { currency ->
                            val toRate    = state.rates[currency] ?: 1.0
                            val converted = (inputAmt / fromRate) * toRate
                            MiniRateCard(
                                currency = currency,
                                amount   = "%.2f".format(converted),
                                modifier = Modifier.weight(1f)
                            )
                            if (pair.last() != currency) {
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        }
                        // Fill empty slot if odd number
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Mini Rate Card ────────────────────────────────────────────────────────────
@Composable
fun MiniRateCard(currency: String, amount: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardNavy)
            .border(1.dp, BorderNavy, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(currency,
                style         = MaterialTheme.typography.labelLarge,
                color         = TextSecondary,
                letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(amount,
                style      = MaterialTheme.typography.titleMedium,
                color      = TextPrimary,
                fontWeight = FontWeight.SemiBold)
        }
    }
}

// ── Currency Dropdown ─────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(
    currencies: List<String>,
    selected: String,
    accentColor: Color,
    onSelect: (String) -> Unit
) {
    var expanded    by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val filtered = currencies.filter {
        it.contains(searchQuery.uppercase().trim())
    }

    ExposedDropdownMenuBox(
        expanded         = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value         = selected,
            onValueChange = {},
            readOnly      = true,
            trailingIcon  = {
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = accentColor
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape  = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = accentColor,
                unfocusedBorderColor = BorderNavy,
                focusedTextColor     = TextPrimary,
                unfocusedTextColor   = TextPrimary
            ),
            textStyle = MaterialTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.Bold)
        )
        ExposedDropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false; searchQuery = "" },
            modifier         = Modifier
                .background(SurfaceNavy)
                .heightIn(max = 300.dp)
        ) {
            // Search inside dropdown
            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value         = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder   = { Text("Search...", color = TextMuted) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(8.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = accentColor,
                        unfocusedBorderColor = BorderNavy,
                        focusedTextColor     = TextPrimary,
                        unfocusedTextColor   = TextPrimary
                    )
                )
            }
            filtered.forEach { currency ->
                DropdownMenuItem(
                    text = {
                        Text(
                            currency,
                            color      = if (currency == selected) accentColor
                            else TextPrimary,
                            fontWeight = if (currency == selected) FontWeight.Bold
                            else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onSelect(currency)
                        expanded    = false
                        searchQuery = ""
                    }
                )
            }
        }
    }
}