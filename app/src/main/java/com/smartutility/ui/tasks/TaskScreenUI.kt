package com.smartutility.ui.tasks

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.smartutility.data.models.TaskEntity
import com.smartutility.ui.theme.*
import com.smartutility.viewmodel.TaskFilter
import com.smartutility.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    navController: NavController,
    vm: TaskViewModel = viewModel()
) {
    val state         by vm.uiState.collectAsStateWithLifecycle()
    val filteredTasks by vm.filteredTasks.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Background orb
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = 80.dp, y = (-60).dp)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF9C6FFF).copy(alpha = 0.08f), Color.Transparent)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Task Manager",
                        style      = MaterialTheme.typography.headlineMedium,
                        color      = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${filteredTasks.count { !it.isCompleted }} tasks remaining",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                // Add task button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(AccentPurple.copy(alpha = 0.15f))
                        .border(1.dp, AccentPurple.copy(alpha = 0.4f), CircleShape)
                        .clickable { vm.openAddDialog() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Task",
                        tint     = AccentPurple,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // ── Summary Cards ────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    label  = "Total",
                    count  = filteredTasks.size,
                    color  = AccentPurple,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    label  = "Active",
                    count  = filteredTasks.count { !it.isCompleted },
                    color  = AccentCyan,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    label  = "Done",
                    count  = filteredTasks.count { it.isCompleted },
                    color  = AccentMint,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Filter Tabs ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskFilter.values().forEach { filter ->
                    val isSelected = filter == state.filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) AccentPurple.copy(alpha = 0.15f)
                                else CardNavy
                            )
                            .border(
                                1.dp,
                                if (isSelected) AccentPurple.copy(alpha = 0.6f)
                                else BorderNavy,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { vm.setFilter(filter) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            filter.name.lowercase()
                                .replaceFirstChar { it.uppercase() },
                            color      = if (isSelected) AccentPurple else TextSecondary,
                            style      = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.SemiBold
                            else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Task List ────────────────────────────────────────────────
            if (filteredTasks.isEmpty()) {
                EmptyState(filter = state.filter, onAdd = { vm.openAddDialog() })
            } else {
                LazyColumn(
                    modifier       = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 20.dp,
                        vertical   = 4.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = filteredTasks,
                        key   = { it.id }
                    ) { task ->
                        AnimatedVisibility(
                            visible = true,
                            enter   = fadeIn() + slideInVertically { 40 }
                        ) {
                            TaskCard(
                                task     = task,
                                onToggle = { vm.toggleCompletion(task) },
                                onEdit   = { vm.openEditDialog(task) },
                                onDelete = { vm.deleteTask(task) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        // ── FAB ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(AccentPurple, AccentPurple.copy(alpha = 0.7f))
                        )
                    )
                    .clickable { vm.openAddDialog() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint     = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // ── Add Dialog ───────────────────────────────────────────────────
        if (state.isAddDialogOpen) {
            TaskDialog(
                title          = "New Task",
                inputTitle     = state.inputTitle,
                inputDesc      = state.inputDescription,
                onTitleChange  = { vm.onTitleChange(it) },
                onDescChange   = { vm.onDescriptionChange(it) },
                onConfirm      = { vm.addTask() },
                onDismiss      = { vm.closeAddDialog() },
                confirmLabel   = "Add Task"
            )
        }

        // ── Edit Dialog ──────────────────────────────────────────────────
        if (state.isEditDialogOpen) {
            TaskDialog(
                title          = "Edit Task",
                inputTitle     = state.inputTitle,
                inputDesc      = state.inputDescription,
                onTitleChange  = { vm.onTitleChange(it) },
                onDescChange   = { vm.onDescriptionChange(it) },
                onConfirm      = { vm.saveEdit() },
                onDismiss      = { vm.closeEditDialog() },
                confirmLabel   = "Save Changes"
            )
        }
    }
}

// ── Task Card ─────────────────────────────────────────────────────────────────
@Composable
fun TaskCard(
    task    : TaskEntity,
    onToggle: () -> Unit,
    onEdit  : () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = CardNavy),
        border   = BorderStroke(
            1.dp,
            if (task.isCompleted) AccentMint.copy(alpha = 0.2f) else BorderNavy
        )
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (task.isCompleted) AccentMint.copy(alpha = 0.2f)
                        else Color.Transparent
                    )
                    .border(
                        2.dp,
                        if (task.isCompleted) AccentMint else BorderNavy,
                        CircleShape
                    )
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Completed",
                        tint     = AccentMint,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text           = task.title,
                    style          = MaterialTheme.typography.titleSmall,
                    color          = if (task.isCompleted) TextSecondary else TextPrimary,
                    fontWeight     = FontWeight.SemiBold,
                    textDecoration = if (task.isCompleted)
                        TextDecoration.LineThrough else TextDecoration.None,
                    maxLines       = 2,
                    overflow       = TextOverflow.Ellipsis
                )

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text     = task.description,
                        style    = MaterialTheme.typography.bodySmall,
                        color    = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Status badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (task.isCompleted) AccentMint.copy(alpha = 0.12f)
                                else AccentPurple.copy(alpha = 0.12f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text          = if (task.isCompleted) "DONE" else "ACTIVE",
                            style         = MaterialTheme.typography.labelSmall,
                            color         = if (task.isCompleted) AccentMint
                            else AccentPurple,
                            fontSize      = 9.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text  = dateFormat.format(Date(task.createdAt)),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick  = { onEdit() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint     = AccentCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick  = { onDelete() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint     = Color(0xFFFF4D6A),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Bottom accent line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            if (task.isCompleted) AccentMint.copy(alpha = 0.4f)
                            else AccentPurple.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

// ── Task Dialog (shared for Add & Edit) ───────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDialog(
    title         : String,
    inputTitle    : String,
    inputDesc     : String,
    onTitleChange : (String) -> Unit,
    onDescChange  : (String) -> Unit,
    onConfirm     : () -> Unit,
    onDismiss     : () -> Unit,
    confirmLabel  : String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = CardNavy,
        shape            = RoundedCornerShape(24.dp),
        title = {
            Text(
                title,
                style      = MaterialTheme.typography.headlineSmall,
                color      = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Title field
                OutlinedTextField(
                    value         = inputTitle,
                    onValueChange = onTitleChange,
                    modifier      = Modifier.fillMaxWidth(),
                    label         = { Text("Title *", color = TextMuted) },
                    placeholder   = { Text("Enter task title", color = TextMuted) },
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = AccentPurple,
                        unfocusedBorderColor = BorderNavy,
                        focusedTextColor     = TextPrimary,
                        unfocusedTextColor   = TextPrimary,
                        cursorColor          = AccentPurple,
                        focusedLabelColor    = AccentPurple,
                        unfocusedLabelColor  = TextMuted
                    )
                )

                // Description field
                OutlinedTextField(
                    value         = inputDesc,
                    onValueChange = onDescChange,
                    modifier      = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    label         = { Text("Description (optional)", color = TextMuted) },
                    placeholder   = { Text("Add details...", color = TextMuted) },
                    maxLines      = 4,
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = AccentPurple,
                        unfocusedBorderColor = BorderNavy,
                        focusedTextColor     = TextPrimary,
                        unfocusedTextColor   = TextPrimary,
                        cursorColor          = AccentPurple,
                        focusedLabelColor    = AccentPurple,
                        unfocusedLabelColor  = TextMuted
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = inputTitle.isNotBlank(),
                shape   = RoundedCornerShape(12.dp),
                colors  = ButtonDefaults.buttonColors(
                    containerColor         = AccentPurple,
                    contentColor           = Color.White,
                    disabledContainerColor = BorderNavy,
                    disabledContentColor   = TextMuted
                )
            ) {
                Text(confirmLabel, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// ── Summary Card ──────────────────────────────────────────────────────────────
@Composable
fun SummaryCard(
    label   : String,
    count   : Int,
    color   : Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardNavy)
            .border(1.dp, BorderNavy, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text      = count.toString(),
                style     = MaterialTheme.typography.headlineMedium,
                color     = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text  = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

// ── Empty State ───────────────────────────────────────────────────────────────
@Composable
fun EmptyState(filter: TaskFilter, onAdd: () -> Unit) {
    Column(
        modifier             = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment  = Alignment.CenterHorizontally,
        verticalArrangement  = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(AccentPurple.copy(alpha = 0.10f))
                .border(1.dp, AccentPurple.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint     = AccentPurple,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text      = when (filter) {
                TaskFilter.ALL       -> "No tasks yet"
                TaskFilter.ACTIVE    -> "No active tasks"
                TaskFilter.COMPLETED -> "No completed tasks"
            },
            style     = MaterialTheme.typography.titleLarge,
            color     = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text  = when (filter) {
                TaskFilter.ALL       -> "Tap the + button to create your first task"
                TaskFilter.ACTIVE    -> "All your tasks are completed!"
                TaskFilter.COMPLETED -> "Complete a task to see it here"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        if (filter == TaskFilter.ALL) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onAdd,
                shape   = RoundedCornerShape(12.dp),
                colors  = ButtonDefaults.buttonColors(
                    containerColor = AccentPurple,
                    contentColor   = Color.White
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null,
                    modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add First Task", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}