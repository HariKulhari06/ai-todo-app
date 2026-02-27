package com.example.todoapp.feature.todo

import com.example.todoapp.feature.todo.domain.model.TodoItem

enum class TodoFilter {
    ALL,
    TODAY,
    IMPORTANT
}

data class TodoUiState(
    val inputText: String = "",
    val selectedFilter: TodoFilter = TodoFilter.ALL,
    val todos: List<TodoItem> = emptyList(),
    val visibleTodos: List<TodoItem> = emptyList(),
    val completedCount: Int = 0
)

sealed interface TodoAction {
    data class InputChanged(val value: String) : TodoAction
    data class FilterSelected(val filter: TodoFilter) : TodoAction
    data class ToggleTodo(val id: String) : TodoAction
    data class AddQuickTodo(val title: String) : TodoAction
    data object AddTodo : TodoAction
}
