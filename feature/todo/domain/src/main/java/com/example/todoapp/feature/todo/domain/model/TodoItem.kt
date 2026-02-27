package com.example.todoapp.feature.todo.domain.model

data class TodoItem(
    val id: String,
    val title: String,
    val category: String,
    val dueLabel: String,
    val isDone: Boolean,
    val isImportant: Boolean
)
