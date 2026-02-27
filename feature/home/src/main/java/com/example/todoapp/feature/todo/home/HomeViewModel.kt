package com.example.todoapp.feature.todo.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.feature.todo.domain.model.TodoItem
import com.example.todoapp.feature.todo.domain.repository.TodoRepository
import com.example.todoapp.feature.todo.domain.usecase.AddTodoUseCase
import com.example.todoapp.feature.todo.domain.usecase.ObserveTodosUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val todos: List<TodoItem> = emptyList()
)

class HomeViewModel(
    private val observeTodosUseCase: ObserveTodosUseCase,
    private val addTodoUseCase: AddTodoUseCase
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = observeTodosUseCase()
        .map { HomeUiState(todos = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    fun addQuickTodo(title: String) {
        val clean = title.trim()
        if (clean.isBlank()) return
        viewModelScope.launch {
            addTodoUseCase(clean)
        }
    }

    companion object {
        fun factory(repository: TodoRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(
                        observeTodosUseCase = ObserveTodosUseCase(repository),
                        addTodoUseCase = AddTodoUseCase(repository)
                    ) as T
                }
            }
        }
    }
}
