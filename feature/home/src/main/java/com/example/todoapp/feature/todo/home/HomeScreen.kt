package com.example.todoapp.feature.todo.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.todoapp.core.designsystem.theme.AppBlack
import com.example.todoapp.core.designsystem.theme.AppDivider
import com.example.todoapp.core.designsystem.theme.AppGray
import com.example.todoapp.core.designsystem.theme.AppOrange
import com.example.todoapp.core.designsystem.theme.AppWhite
import com.example.todoapp.core.designsystem.theme.CardLight
import com.example.todoapp.core.navigation.DashboardDestination
import com.example.todoapp.feature.todo.data.di.TodoRepositoryProvider
import com.example.todoapp.feature.todo.domain.model.TodoItem

private data class ProjectCardUi(
    val id: String,
    val category: String,
    val title: String,
    val subtitle: String,
    val hours: String,
    val progress: Int,
    val background: Color,
    val foreground: Color,
    val isDone: Boolean
)

fun NavGraphBuilder.homeScreen(onCardClick: (String) -> Unit) {
    composable(route = DashboardDestination.route) {
        val viewModel = rememberHomeViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val cards = uiState.todos.mapIndexed(::toCard)

        DashboardScreen(
            cards = cards,
            onAddTask = {
                val nextNumber = uiState.todos.size + 1
                viewModel.addQuickTodo("New Task $nextNumber")
            },
            onCardClick = onCardClick
        )
    }
}

@Composable
private fun rememberHomeViewModel(): HomeViewModel {
    val repository = remember { TodoRepositoryProvider.repository }
    return viewModel(factory = HomeViewModel.factory(repository))
}

private fun toCard(index: Int, todo: TodoItem): ProjectCardUi {
    val palette = listOf(
        AppOrange to AppWhite,
        CardLight to AppBlack,
        Color(0xFF4967E7) to AppWhite
    )
    val (background, foreground) = palette[index % palette.size]
    return ProjectCardUi(
        id = todo.id,
        category = todo.category.ifBlank { "General" },
        title = todo.title,
        subtitle = when {
            todo.isDone -> "Completed task"
            todo.isImportant -> "High priority task"
            else -> "Planned for ${todo.dueLabel.lowercase()}"
        },
        hours = if (todo.isDone) "Done" else "2 Hours",
        progress = if (todo.isDone) 100 else if (todo.isImportant) 76 else 58,
        background = background,
        foreground = foreground,
        isDone = todo.isDone
    )
}

@Composable
private fun DashboardScreen(cards: List<ProjectCardUi>, onAddTask: () -> Unit, onCardClick: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        color = AppBlack
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hello Shanaws,", color = AppGray, style = MaterialTheme.typography.bodyLarge)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(AppOrange)
                            .clickable(onClick = onAddTask),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = AppWhite)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Complete your today's tasks!",
                    color = AppWhite,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 54.sp, lineHeight = 58.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                DateStrip()
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(cards, key = { it.id }) { card ->
                DashboardTaskCard(card = card, onViewClick = { onCardClick(card.id) })
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun DateStrip() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        DateCell("24 Feb", "Friday", false)
        DateCell("25 Feb", "Saturday", false)
        DateCell("26 Feb", "Sunday", true)
        DateCell("27 Feb", "Monday", false)
        DateCell("28 Feb", "Tuesday", false)
    }
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider(color = AppDivider)
}

@Composable
private fun DateCell(day: String, label: String, selected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(day, color = if (selected) AppWhite else AppGray, style = MaterialTheme.typography.bodyMedium)
        Text(label, color = if (selected) AppWhite else AppGray, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(5.dp))
        if (selected) {
            Box(
                modifier = Modifier
                    .width(62.dp)
                    .height(2.dp)
                    .clip(CircleShape)
                    .background(AppOrange)
            )
        }
    }
}

@Composable
private fun DashboardTaskCard(card: ProjectCardUi, onViewClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = card.background)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(card.category, color = card.foreground.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(card.title, color = card.foreground, style = MaterialTheme.typography.titleLarge.copy(fontSize = 36.sp, lineHeight = 40.sp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(card.subtitle, color = card.foreground.copy(alpha = 0.82f), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Badge(text = card.hours, textColor = card.foreground, outline = card.foreground.copy(alpha = 0.55f))
                Spacer(modifier = Modifier.width(8.dp))
                Badge(text = "${card.progress}%", textColor = card.foreground, outline = card.foreground.copy(alpha = 0.55f))
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onViewClick,
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppBlack),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("View", color = AppWhite)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = AppWhite, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun Badge(text: String, textColor: Color, outline: Color) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(100.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, outline)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
