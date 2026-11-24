package com.kelompok.coroutinedemo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok.coroutinedemo.data.Task
import com.kelompok.coroutinedemo.data.TaskRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.system.measureTimeMillis

class TasksViewModel(
    private val repository: TaskRepository = TaskRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadData(shouldFail: Boolean = false) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null, loadDurationMs = null)
            }
            try {
                var loadedTasks = emptyList<Task>()
                var loadedAnnouncement = ""
                var loadedCourseCount = 0

                val duration = measureTimeMillis {
                    coroutineScope {
                        val tasksDeferred = async { repository.getTasks(shouldFail) }
                        val announcementDeferred = async { repository.getAnnouncement() }
                        val courseCountDeferred = async { repository.getCourseCount() }

                        loadedTasks = tasksDeferred.await()
                        loadedAnnouncement = announcementDeferred.await()
                        loadedCourseCount = courseCountDeferred.await()
                    }
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        tasks = loadedTasks,
                        announcement = loadedAnnouncement,
                        courseCount = loadedCourseCount,
                        loadDurationMs = duration
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load: ${e.message}"
                    )
                }
            }
        }
    }

    fun cancelLoading() {
        loadJob?.cancel()
        _uiState.update {
            it.copy(isLoading = false, errorMessage = "Loading cancelled")
        }
    }

    fun sortTasksByTitle() {
        val currentTasks = _uiState.value.tasks
        if (currentTasks.isEmpty()) return

        viewModelScope.launch {
            val ascending = _uiState.value.isSortedAscending
            val sortedList = withContext(Dispatchers.Default) {
                if (ascending) {
                    currentTasks.sortedBy { it.title.lowercase() }
                } else {
                    currentTasks.sortedByDescending { it.title.lowercase() }
                }
            }
            _uiState.update {
                it.copy(
                    tasks = sortedList,
                    isSortedAscending = !ascending
                )
            }
        }
    }

    fun addNewTask(title: String, course: String) {
        if (title.isBlank() || course.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val newTask = repository.addTask(title.trim(), course.trim())
            _uiState.update {
                it.copy(
                    isSaving = false,
                    tasks = listOf(newTask) + it.tasks
                )
            }
        }
    }

    fun toggleTaskCompleted(taskId: Int) {
        _uiState.update { state ->
            state.copy(
                tasks = state.tasks.map { task ->
                    if (task.id == taskId) task.copy(isCompleted = !task.isCompleted) else task
                }
            )
        }
    }

    fun onTap() {
        _uiState.update { it.copy(tapCount = it.tapCount + 1) }
    }

    fun blockMainThread() {
        Thread.sleep(3000L)
    }
}