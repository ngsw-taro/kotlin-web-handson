package com.example.presentation

import com.example.domain.Task
import com.example.domain.TaskRepository
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineInterceptor
import java.time.LocalDateTime

private typealias  Handler = PipelineInterceptor<Unit, ApplicationCall>

class TaskController(
    private val taskRepository: TaskRepository
) {

    fun index(): Handler = {
        val tasks = taskRepository.findTasks()
        call.respond(tasks)
    }

    fun show(): Handler = {
        val id = call.parameters["id"]?.toLongOrNull()
        val task = id?.let(taskRepository::findTaskById)
        if (task == null) {
            call.respond(HttpStatusCode.NotFound, "not found")
        } else {
            call.respond(task)
        }
    }

    fun create(): Handler = handler@{
        val body = call.receive<TaskCreateBody>()
        val violations = body.validate()
        if (violations.isValid.not()) {
            call.response.status(HttpStatusCode.BadRequest)
            return@handler
        }

        val task = Task.create(
            title = body.title,
            description = body.description
        )
        taskRepository.storeTask(task)
        call.response.status(HttpStatusCode.Created)
    }

    fun update(): Handler = handler@{
        val id = call.parameters["id"]?.toLongOrNull()
        val task = id?.let(taskRepository::findTaskById)
        if (task == null) {
            call.respond(HttpStatusCode.NotFound, "not found")
            return@handler
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