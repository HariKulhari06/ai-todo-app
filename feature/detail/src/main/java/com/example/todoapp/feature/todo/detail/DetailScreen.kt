package com.example.todoapp.feature.todo.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.todoapp.core.designsystem.theme.AppBlack
import com.example.todoapp.core.designsystem.theme.AppDivider
import com.example.todoapp.core.designsystem.theme.AppGray
import com.example.todoapp.core.designsystem.theme.AppOrange
import com.example.todoapp.core.designsystem.theme.AppPanelSoft
import com.example.todoapp.core.designsystem.theme.AppWhite
import com.example.todoapp.core.designsystem.theme.CardLight
import com.example.todoapp.core.navigation.DetailDestination
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

fun NavGraphBuilder.detailScreen(onClose: () -> Unit) {
    composable(
        route = DetailDestination.route,
        arguments = listOf(navArgument(DetailDestination.ARG_PROJECT_ID) { type = NavType.StringType })
    ) { backStackEntry ->
        val viewModel = rememberDetailViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val cardId = backStackEntry.arguments?.getString(DetailDestination.ARG_PROJECT_ID).orEmpty()
        val todo = uiState.todos.firstOrNull { it.id == cardId }
        val card = todo?.let { toCard(0, it) }

        if (todo != null && card != null) {
            DetailScreen(
                card = card,
                onClose = onClose,
                onMarkComplete = {
                    if (!todo.isDone) {
                        viewModel.toggleTodo(todo.id)
                    }
                }
            )
        }
    }
}

@Composable
private fun rememberDetailViewModel(): DetailViewModel {
    val repository = remember { TodoRepositoryProvider.repository }
    return viewModel(factory = DetailViewModel.factory(repository))
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
private fun DetailScreen(card: ProjectCardUi, onClose: () -> Unit, onMarkComplete: () -> Unit) {
    val taskItems = listOf(
        "Choose a design shot idea" to true,
        "Choose colors, fonts & assets" to true,
        "Design 3 Screens" to card.isDone,
        "Choose a design mockup" to card.isDone,
        "Create a shot presentation" to false
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        color = AppBlack
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(card.category, color = AppGray, style = MaterialTheme.typography.bodyLarge)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AppPanelSoft)
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = AppGray)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = detailHeadline(card.title),
                color = AppOrange,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 56.sp, lineHeight = 58.sp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Badge(text = "Logix Mates", textColor = AppWhite, outline = AppGray)
                Column(horizontalAlignment = Alignment.End) {
                    Text("Time Left", color = AppGray, style = MaterialTheme.typography.bodyMedium)
                    Text("1Hr 24 Min", color = AppWhite, style = MaterialTheme.typography.titleMedium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppPanelSoft)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth((if (card.isDone) 100 else card.progress) / 100f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppOrange)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Progress", color = AppGray, style = MaterialTheme.typography.bodyLarge)
                Text("${if (card.isDone) 100 else card.progress}%", color = AppWhite, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = AppDivider)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Additional Information", color = AppGray, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(card.subtitle, color = AppWhite, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = AppDivider)
            Spacer(modifier = Modifier.height(14.dp))

            Text("Your Tasks", color = AppWhite, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(10.dp))

            taskItems.forEach { (task, done) ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (done) AppOrange else AppPanelSoft)
                            .border(
                                width = if (done) 0.dp else 1.dp,
                                color = AppDivider,
                                shape = RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (done) Icon(Icons.Default.Check, contentDescription = null, tint = AppWhite, modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = task,
                        color = if (done) AppGray else AppWhite,
                        textDecoration = if (done) TextDecoration.LineThrough else TextDecoration.None,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onMarkComplete,
                enabled = !card.isDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppOrange)
            ) {
                Text(if (card.isDone) "Completed" else "Mark as complete", color = AppWhite, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.Check, contentDescription = null, tint = AppWhite)
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

private fun detailHeadline(title: String): String {
    val firstSpace = title.indexOf(' ')
    return if (firstSpace == -1) title else title.substring(0, firstSpace) + "" + title.substring(firstSpace + 1)
}
