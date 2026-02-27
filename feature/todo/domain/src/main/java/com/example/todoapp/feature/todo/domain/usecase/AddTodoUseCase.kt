package com.example.todoapp.feature.todo.domain.usecase

import com.example.todoapp.feature.todo.domain.repository.TodoRepository

class AddTodoUseCase(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(title: String) {
        repository.addTodo(title)
    }
}
