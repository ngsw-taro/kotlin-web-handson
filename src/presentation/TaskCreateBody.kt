package com.example.presentation

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.core.ConstraintViolations
import am.ik.yavi.core.Validator

class TaskCreateBody(val title: String, val description: String) {
    companion object {
        private val validator: Validator<TaskCreateBody> = ValidatorBuilder.of<TaskCreateBody>()
            .konstraint(TaskCreateBody::title) {
                notBlank()
            }
            .build()
    }

    fun validate(): ConstraintViolations {
        return validator.validate(this)
    }
}