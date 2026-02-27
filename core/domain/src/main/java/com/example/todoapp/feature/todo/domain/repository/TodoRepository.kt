package com.example.todoapp.feature.todo.domain.repository

import com.example.todoapp.feature.todo.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun observeTodos(): Flow<List<TodoItem>>
    suspend fun addTodo(title: String)
    suspend fun toggleTodo(id: String)
}
