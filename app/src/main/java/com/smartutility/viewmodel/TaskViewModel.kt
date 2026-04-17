package com.smartutility.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartutility.data.AppDatabase
import com.smartutility.data.models.TaskEntity
import com.smartutility.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class TaskFilter { ALL, ACTIVE, COMPLETED }

data class TaskUiState(
    val tasks        : List<TaskEntity> = emptyList(),
    val filter       : TaskFilter       = TaskFilter.ALL,
    val isAddDialogOpen  : Boolean      = false,
    val isEditDialogOpen : Boolean      = false,
    val editingTask  : TaskEntity?      = null,
    val inputTitle   : String           = "",
    val inputDescription : String       = ""
)

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository by lazy {
        val dao = AppDatabase.getInstance(application).taskDao()
        TaskRepository(dao)
    }

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    // filtered tasks derived from all tasks + current filter
    val filteredTasks: StateFlow<List<TaskEntity>> = _uiState
        .combine(repository.allTasks) { state, tasks ->
            when (state.filter) {
                TaskFilter.ALL       -> tasks
                TaskFilter.ACTIVE    -> tasks.filter { !it.isCompleted }
                TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
            }
        }
        .stateIn(
            scope         = viewModelScope,
            started       = SharingStarted.WhileSubscribed(5000),
            initialValue  = emptyList()
        )

    // ── Dialog controls ───────────────────────────────────────────────────────

    fun openAddDialog() {
        _uiState.update {
            it.copy(
                isAddDialogOpen     = true,
                inputTitle          = "",
                inputDescription    = ""
            )
        }
    }

    fun closeAddDialog() {
        _uiState.update { it.copy(isAddDialogOpen = false) }
    }

    fun openEditDialog(task: TaskEntity) {
        _uiState.update {
            it.copy(
                isEditDialogOpen = true,
                editingTask      = task,
                inputTitle       = task.title,
                inputDescription = task.description
            )
        }
    }

    fun closeEditDialog() {
        _uiState.update {
            it.copy(isEditDialogOpen = false, editingTask = null)
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(inputTitle = value) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(inputDescription = value) }
    }

    fun setFilter(filter: TaskFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    // ── CRUD operations ───────────────────────────────────────────────────────

    fun addTask() {
        val state = _uiState.value
        if (state.inputTitle.isBlank()) return
        viewModelScope.launch {
            repository.addTask(
                title       = state.inputTitle.trim(),
                description = state.inputDescription.trim()
            )
        }
        closeAddDialog()
    }

    fun saveEdit() {
        val state = _uiState.value
        val task  = state.editingTask ?: return
        if (state.inputTitle.isBlank()) return
        viewModelScope.launch {
            repository.updateTask(
                task.copy(
                    title       = state.inputTitle.trim(),
                    description = state.inputDescription.trim()
                )
            )
        }
        closeEditDialog()
    }

    fun toggleCompletion(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleCompletion(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}