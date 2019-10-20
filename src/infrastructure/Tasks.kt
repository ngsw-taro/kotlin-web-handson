package com.example.infrastructure

import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.joda.time.DateTime

object Tasks : LongIdTable() {
    val title: Column<String> = text("title")
    val description: Column<String> = text("description")
    val createdDateTime: Column<DateTime> = datetime("created_date_time")
    val updatedDateTime: Column<DateTime> = datetime("updated_date_time")
    val completedDateTime: Column<DateTime?> = datetime("completed_date_time").nullable()
}