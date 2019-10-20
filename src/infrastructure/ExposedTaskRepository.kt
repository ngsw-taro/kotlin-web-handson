package com.example.infrastructure

import com.example.domain.Task
import com.example.domain.TaskRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.time.LocalDateTime
import java.time.ZoneId

class ExposedTaskRepository : TaskRepository {
    override fun findTasks(): List<Task> = transaction {
        Tasks.selectAll()
            .orderBy(Tasks.createdDateTime, SortOrder.DESC)
            .map(::toTask)
    }

    override fun findTaskById(id: Long): Task? = transaction {
        Tasks.select { Tasks.id eq id }.map(::toTask).firstOrNull()
    }

    override fun storeTask(task: Task) {
        transaction {
            if (task.id == null) {
                insertTask(task)
            } else {
                updateTask(task)
            }
        }
    }

    override fun deleteTask(task: Task) {
        transaction {
            Tasks.deleteWhere { Tasks.id eq task.id }
        }
    }

    private fun toTask(resultRow: ResultRow): Task {
        return Task(
            id = resultRow[Tasks.id].value,
            title = resultRow[Tasks.title],
            description = resultRow[Tasks.description],
            createdDateTime = resultRow[Tasks.createdDateTime].toStandardLocalDateTime(),
            updatedDateTime = resultRow[Tasks.updatedDateTime].toStandardLocalDateTime(),
            completedDateTime = resultRow[Tasks.completedDateTime]?.toStandardLocalDateTime()
        )
    }

    private fun insertTask(task: Task) {
        val id = Tasks.insertAndGetId {
            it[title] = task.title
            it[description] = task.description
            it[createdDateTime] = task.createdDateTime.toJodaDateTime()
            it[updatedDateTime] = task.updatedDateTime.toJodaDateTime()
            it[completedDateTime] = task.completedDateTime?.toJodaDateTime()
        }
        task.id = id.value
    }

    private fun updateTask(task: Task) {
        Tasks.update({ Tasks.id eq task.id }) {
            it[title] = task.title
            it[description] = task.description
            it[createdDateTime] = task.createdDateTime.toJodaDateTime()
            it[updatedDateTime] = task.updatedDateTime.toJodaDateTime()
            it[completedDateTime] = task.completedDateTime?.toJodaDateTime()
        }
    }

    private fun DateTime.toStandardLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofInstant(toDate().toInstant(), ZoneId.systemDefault())
    }

    private fun LocalDateTime.toJodaDateTime(): DateTime {
        return DateTime(atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
    }
}