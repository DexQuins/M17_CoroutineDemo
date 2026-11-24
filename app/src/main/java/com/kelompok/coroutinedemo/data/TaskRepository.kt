package com.kelompok.coroutinedemo.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException

class TaskRepository {

    suspend fun getTasks(shouldFail: Boolean = false): List<Task> =
        withContext(Dispatchers.IO) {
            delay(2000L)
            if (shouldFail) {
                throw IOException("Server is not reachable")
            }
            listOf(
                Task(1, "Write lab report", "Mobile App Development"),
                Task(2, "Read chapter 5", "Data Science"),
                Task(3, "Prepare quiz", "Research Methodology"),
                Task(4, "Design UI wireframe", "Human Computer Interaction")
            )
        }

    suspend fun getAnnouncement(): String =
        withContext(Dispatchers.IO) {
            delay(1000L)
            "Quiz 2 is next Monday"
        }

    // Latihan 1: Tambah permintaan getCourseCount()
    suspend fun getCourseCount(): Int =
        withContext(Dispatchers.IO) {
            delay(1500L)
            4
        }

    // Modifikasi: Simulasi menyimpan tugas baru ke server secara asinkron
    suspend fun addTask(title: String, course: String): Task =
        withContext(Dispatchers.IO) {
            delay(800L)
            Task(
                id = (100..999).random(),
                title = title,
                course = course
            )
        }
}