package com.example.todoapp.feature.todo.data.di

import com.example.todoapp.feature.todo.data.repository.InMemoryTodoRepository
import com.example.todoapp.feature.todo.domain.repository.TodoRepository

object TodoRepositoryProvider {
    val repository: TodoRepository by lazy { InMemoryTodoRepository() }
}
