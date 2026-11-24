package com.kelompok.coroutinedemo.ui.main

import com.kelompok.coroutinedemo.data.Task

data class TasksUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val announcement: String = "",
    val courseCount: Int = 0,
    val isSortedAscending: Boolean = true,
    val loadDurationMs: Long? = null,
    val errorMessage: String? = null,
    val tapCount: Int = 0
)