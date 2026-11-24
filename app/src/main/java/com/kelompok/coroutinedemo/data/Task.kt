package com.kelompok.coroutinedemo.data

data class Task(
    val id: Int,
    val title: String,
    val course: String,
    val isCompleted: Boolean = false // Modifikasi: status selesai
)