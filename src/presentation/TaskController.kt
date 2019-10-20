package com.example.presentation

import com.example.domain.Task
import com.example.domain.TaskRepository
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import java.time.LocalDateTime

class TaskController(
    private val taskRepository: TaskRepository
) {

    suspend fun index(call: ApplicationCall) {
        val tasks = taskRepository.findTasks()
        call.respond(tasks)
    }

    suspend fun show(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull()
        val task = id?.let(taskRepository::findTaskById)
        if (task == null) {
            call.respond(HttpStatusCode.NotFound, "not found")
        } else {
            call.respond(task)
        }
    }

    suspend fun create(call: ApplicationCall) {
        val body = call.receive<TaskCreateBody>()
        val violations = body.validate()
        if (violations.isValid.not()) {
            call.response.status(HttpStatusCode.BadRequest)
            return
        }

        val task = Task.create(
            title = body.title,
            description = body.description
        )
        taskRepository.storeTask(task)
        call.response.status(HttpStatusCode.Created)
    }

    suspend fun update(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull()
        val task = id?.let(taskRepository::findTaskById)
        if (task == null) {
            call.respond(HttpStatusCode.NotFound, "not found")
            return
        }

        val body = call.receive<TaskUpdateBody>()
        task.title = body.title
        task.description = body.description
        task.updatedDateTime = LocalDateTime.now()
        task.isCompleted = body.completed

        taskRepository.storeTask(task)
        call.response.status(HttpStatusCode.NoContent)
    }
}