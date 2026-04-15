package com.smartutility.ui.converter

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
import com.smartutility.data.models.ConversionCategory
import com.smartutility.data.models.ConversionUnit
import com.smartutility.ui.theme.*
import com.smartutility.viewmodel.ConverterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    navController: NavController,
    vm: ConverterViewModel = viewModel()
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
                .size(260.dp)
                .offset(x = (-80).dp, y = 80.dp)
                .background(
                    Brush.radialGradient(
                        listOf(AccentCyan.copy(alpha = 0.08f), Color.Transparent)
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
                Column {
                    Text("Unit Converter",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Convert between units instantly",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Category Tabs ────────────────────────────────────────────
            ScrollableTabRow(
                selectedTabIndex = ConversionCategory.values()
                    .indexOf(state.selectedCategory),
                modifier        = Modifier.fillMaxWidth(),
                containerColor  = Color.Transparent,
                contentColor    = AccentCyan,
                edgePadding     = 16.dp,
                divider         = {}
            ) {
                ConversionCategory.values().forEach { cat ->
                    val isSelected = cat == state.selectedCategory
                    Tab(
                        selected = isSelected,
                        onClick  = { vm.selectCategory(cat) },
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) AccentCyan.copy(alpha = 0.15f)
                                    else CardNavy
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) AccentCyan.copy(alpha = 0.6f)
                                    else BorderNavy,
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                cat.label,
                                color = if (isSelected) AccentCyan else TextSecondary,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.SemiBold
                                else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Main Card ────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape  = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                border = BorderStroke(1.dp, BorderNavy)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // FROM unit
                    Text("FROM", style = MaterialTheme.typography.labelMedium,
                        color = TextMuted, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    UnitDropdown(
                        units       = state.selectedCategory.units,
                        selected    = state.fromUnit,
                        accentColor = AccentCyan,
                        onSelect    = { vm.setFromUnit(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Input field
                    OutlinedTextField(
                        value         = state.inputValue,
                        onValueChange = { vm.onInputChange(it) },
                        modifier      = Modifier.fillMaxWidth(),
                        placeholder   = { Text("Enter value", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        singleLine = true,
                        shape      = RoundedCornerShape(12.dp),
                        colors     = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = AccentCyan,
                            unfocusedBorderColor = BorderNavy,
                            focusedTextColor     = TextPrimary,
                            unfocusedTextColor   = TextPrimary,
                            cursorColor          = AccentCyan
                        ),
                        textStyle = MaterialTheme.typography.headlineMedium
                    )

                    // Swap button
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier         = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        FilledTonalButton(
                            onClick = { vm.swapUnits() },
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

                    // TO unit
                    Text("TO", style = MaterialTheme.typography.labelMedium,
                        color = TextMuted, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    UnitDropdown(
                        units       = state.selectedCategory.units,
                        selected    = state.toUnit,
                        accentColor = AccentCyan,
                        onSelect    = { vm.setToUnit(it) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Result box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(AccentCyan.copy(alpha = 0.08f))
                            .border(
                                1.dp,
                                AccentCyan.copy(alpha = 0.25f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Text("RESULT",
                                style         = MaterialTheme.typography.labelMedium,
                                color         = AccentCyan,
                                letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            if (state.result.isNotEmpty()) {
                                Text(
                                    "${state.result} ${state.toUnit.symbol}",
                                    style      = MaterialTheme.typography.displaySmall,
                                    color      = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "${state.inputValue} ${state.fromUnit.symbol}" +
                                            " = ${state.result} ${state.toUnit.symbol}",
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
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Unit Dropdown ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdown(
    units: List<ConversionUnit>,
    selected: ConversionUnit,
    accentColor: Color,
    onSelect: (ConversionUnit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded          = expanded,
        onExpandedChange  = { expanded = it }
    ) {
        OutlinedTextField(
            value       = "${selected.name} (${selected.symbol})",
            onValueChange = {},
            readOnly    = true,
            trailingIcon = {
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
            )
        )
        ExposedDropdownMenu(
            expanded          = expanded,
            onDismissRequest  = { expanded = false },
            modifier          = Modifier.background(SurfaceNavy)
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                unit.name,
                                color      = if (unit == selected) accentColor
                                else TextPrimary,
                                fontWeight = if (unit == selected) FontWeight.SemiBold
                                else FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(unit.symbol, color = TextSecondary,
                                style = MaterialTheme.typography.bodyMedium)
                        }
                    },
                    onClick = {
                        onSelect(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}