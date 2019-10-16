package com.example.domain

import java.time.LocalDateTime

class Task(
    var id: Long?,
    var title: String,
    var description: String,
    val createdDateTime: LocalDateTime,
    var updatedDateTime: LocalDateTime,
    completedDateTime: LocalDateTime?
) {

    var completedDateTime: LocalDateTime? = completedDateTime
        private set

    var isCompleted: Boolean
        set(value) {
            completedDateTime = if (value) LocalDateTime.now() else null
        }
        get() = completedDateTime != null

    companion object {
        fun create(title: String, description: String): Task {
            val now = LocalDateTime.now()
            return Task(
                id = null,
                title = title,
                description = description,
                createdDateTime = now,
                updatedDateTime = now,
                completedDateTime = null
            )
        }
    }
}