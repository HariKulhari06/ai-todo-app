package com.example.todoapp.feature.todo.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.feature.todo.domain.model.TodoItem
import com.example.todoapp.feature.todo.domain.repository.TodoRepository
import com.example.todoapp.feature.todo.domain.usecase.ObserveTodosUseCase
import com.example.todoapp.feature.todo.domain.usecase.ToggleTodoUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetailUiState(
    val todos: List<TodoItem> = emptyList()
)

class DetailViewModel(
    private val observeTodosUseCase: ObserveTodosUseCase,
    private val toggleTodoUseCase: ToggleTodoUseCase
) : ViewModel() {

    val uiState: StateFlow<DetailUiState> = observeTodosUseCase()
        .map { DetailUiState(todos = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailUiState()
        )

    fun toggleTodo(id: String) {
        viewModelScope.launch {
            toggleTodoUseCase(id)
        }
    }

    companion object {
        fun factory(repository: TodoRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DetailViewModel(
                        observeTodosUseCase = ObserveTodosUseCase(repository),
                        toggleTodoUseCase = ToggleTodoUseCase(repository)
                    ) as T
                }
            }
        }
    }
}
