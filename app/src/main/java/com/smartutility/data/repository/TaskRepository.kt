package com.smartutility.data.repository

import com.smartutility.data.models.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun addTask(title: String, description: String) {
        taskDao.insertTask(
            TaskEntity(title = title, description = description)
        )
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    suspend fun toggleCompletion(task: TaskEntity) {
        taskDao.updateCompletion(task.id, !task.isCompleted)
    }
}