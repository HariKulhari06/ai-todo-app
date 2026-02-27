package com.example.todoapp.feature.todo.data.repository

import com.example.todoapp.feature.todo.domain.model.TodoItem
import com.example.todoapp.feature.todo.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryTodoRepository : TodoRepository {
    private val todos = MutableStateFlow(
        listOf(
            TodoItem("1", "Design onboarding screens", "Work", "Today", false, true),
            TodoItem("2", "30-minute run", "Health", "Today", false, false),
            TodoItem("3", "Book dentist appointment", "Personal", "This week", true, false),
            TodoItem("4", "Review sprint plan", "Work", "Today", false, true)
        )
    )

    override fun observeTodos(): Flow<List<TodoItem>> = todos.asStateFlow()

    override suspend fun addTodo(title: String) {
        val newTodo = TodoItem(
            id = System.currentTimeMillis().toString(),
            title = title,
            category = "Inbox",
            dueLabel = "Today",
            isDone = false,
            isImportant = false
        )
        todos.update { current -> listOf(newTodo) + current }
    }

    override suspend fun toggleTodo(id: String) {
        todos.update { current ->
            current.map { todo ->
                if (todo.id == id) todo.copy(isDone = !todo.isDone) else todo
            }
        }
    }
}
