package com.example.todoapp.feature.todo.domain.usecase

import com.example.todoapp.feature.todo.domain.model.TodoItem
import com.example.todoapp.feature.todo.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

class ObserveTodosUseCase(
    private val repository: TodoRepository
) {
    operator fun invoke(): Flow<List<TodoItem>> = repository.observeTodos()
}
