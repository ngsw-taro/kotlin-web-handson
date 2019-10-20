package com.example.domain

interface TaskRepository {
    fun findTasks(): List<Task>
    fun findTaskById(id: Long): Task?
    fun storeTask(task: Task)
    fun deleteTask(task: Task)
}