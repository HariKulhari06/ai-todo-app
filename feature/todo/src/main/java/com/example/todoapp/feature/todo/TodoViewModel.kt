package com.example.todoapp.feature.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.feature.todo.domain.model.TodoItem
import com.example.todoapp.feature.todo.domain.repository.TodoRepository
import com.example.todoapp.feature.todo.domain.usecase.AddTodoUseCase
import com.example.todoapp.feature.todo.domain.usecase.ObserveTodosUseCase
import com.example.todoapp.feature.todo.domain.usecase.ToggleTodoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodoViewModel(
    private val observeTodosUseCase: ObserveTodosUseCase,
    private val addTodoUseCase: AddTodoUseCase,
    private val toggleTodoUseCase: ToggleTodoUseCase
) : ViewModel() {

    private val inputText = MutableStateFlow("")
    private val selectedFilter = MutableStateFlow(TodoFilter.ALL)

    val uiState = combine(
        observeTodosUseCase(),
        inputText,
        selectedFilter
    ) { todos, input, filter ->
        val visible = filterTodos(todos = todos, filter = filter, query = input)
        TodoUiState(
            inputText = input,
            selectedFilter = filter,
            todos = todos,
            visibleTodos = visible,
            completedCount = todos.count { it.isDone }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TodoUiState()
    )

    fun onAction(action: TodoAction) {
        when (action) {
            is TodoAction.InputChanged -> inputText.update { action.value }
            is TodoAction.FilterSelected -> selectedFilter.update { action.filter }
            is TodoAction.ToggleTodo -> toggleTodo(action.id)
            is TodoAction.AddQuickTodo -> addQuickTodo(action.title)
            TodoAction.AddTodo -> addTodo()
        }
    }

    private fun addTodo() {
        val title = inputText.value.trim()
        if (title.isBlank()) return

        viewModelScope.launch {
            addTodoUseCase(title)
            inputText.update { "" }
        }
    }

    private fun toggleTodo(id: String) {
        viewModelScope.launch {
            toggleTodoUseCase(id)
        }
    }

    private fun addQuickTodo(title: String) {
        val clean = title.trim()
        if (clean.isBlank()) return
        viewModelScope.launch {
            addTodoUseCase(clean)
        }
    }

    private fun filterTodos(
        todos: List<TodoItem>,
        filter: TodoFilter,
        query: String
    ): List<TodoItem> {
        val filteredByChip = when (filter) {
            TodoFilter.ALL -> todos
            TodoFilter.TODAY -> todos.filter { it.dueLabel.equals("Today", ignoreCase = true) }
            TodoFilter.IMPORTANT -> todos.filter { it.isImportant }
        }

        if (query.isBlank()) return filteredByChip
        return filteredByChip.filter { it.title.contains(query.trim(), ignoreCase = true) }
    }

    companion object {
        fun factory(repository: TodoRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TodoViewModel(
                        observeTodosUseCase = ObserveTodosUseCase(repository),
                        addTodoUseCase = AddTodoUseCase(repository),
                        toggleTodoUseCase = ToggleTodoUseCase(repository)
                    ) as T
                }
            }
        }
    }
}
