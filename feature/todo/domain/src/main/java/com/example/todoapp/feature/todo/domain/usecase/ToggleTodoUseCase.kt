package com.example.todoapp.feature.todo.domain.usecase

import com.example.todoapp.feature.todo.domain.repository.TodoRepository

class ToggleTodoUseCase(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(id: String) {
        repository.toggleTodo(id)
    }
}
