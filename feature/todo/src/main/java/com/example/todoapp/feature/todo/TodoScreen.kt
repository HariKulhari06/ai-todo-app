package com.example.todoapp.feature.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
import com.example.todoapp.core.designsystem.theme.TodoAppTheme
import com.example.todoapp.core.navigation.DashboardDestination
import com.example.todoapp.core.navigation.DetailDestination
import com.example.todoapp.core.navigation.IntroDestination
import com.example.todoapp.feature.todo.data.di.TodoRepositoryProvider
import com.example.todoapp.feature.todo.domain.model.TodoItem
import kotlin.math.roundToInt

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

fun NavGraphBuilder.todoScreen(navController: NavController) {
    composable(route = IntroDestination.route) {
        IntroScreen(onGetStarted = { navController.navigate(DashboardDestination.route) })
    }

    composable(route = DashboardDestination.route) {
        val viewModel = rememberTodoViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val cards = uiState.todos.mapIndexed(::toCard)

        DashboardScreen(
            cards = cards,
            onAddTask = {
                val nextNumber = uiState.todos.size + 1
                viewModel.onAction(TodoAction.AddQuickTodo("New Task $nextNumber"))
            },
            onCardClick = { cardId -> navController.navigate(DetailDestination.createRoute(cardId)) }
        )
    }

    composable(
        route = DetailDestination.route,
        arguments = listOf(navArgument(DetailDestination.ARG_PROJECT_ID) { type = NavType.StringType })
    ) { backStackEntry ->
        val viewModel = rememberTodoViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val cardId = backStackEntry.arguments?.getString(DetailDestination.ARG_PROJECT_ID).orEmpty()
        val todo = uiState.todos.firstOrNull { it.id == cardId }
        val card = todo?.let { toCard(0, it) }

        if (todo != null && card != null) {
            DetailScreen(
                card = card,
                onClose = { navController.popBackStack() },
                onMarkComplete = {
                    if (!todo.isDone) {
                        viewModel.onAction(TodoAction.ToggleTodo(todo.id))
                    }
                }
            )
        }
    }
}

@Composable
private fun rememberTodoViewModel(): TodoViewModel {
    val repository = remember { TodoRepositoryProvider.repository }
    return viewModel(factory = TodoViewModel.factory(repository))
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
private fun IntroScreen(onGetStarted: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBlack)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .width(84.dp)
                            .height(3.dp)
                            .clip(CircleShape)
                            .background(if (it == 0) Color(0xFFB3B3B3) else Color(0xFF5C5C5C))
                    )
                }
                Box(
                    modifier = Modifier
                        .width(84.dp)
                        .height(3.dp)
                        .clip(CircleShape)
                        .background(AppOrange)
                )
            }

            Spacer(modifier = Modifier.height(56.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x22FFFFFF), Color(0x05000000), Color.Transparent)
                        )
                    )
            )

            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Complete your tasks", color = AppGray, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Easily manage\nyour to-do list\nand take\ncontrol of your\nschedule",
                color = AppWhite,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(26.dp))
            SlideToStartButton(onCompleted = onGetStarted)
        }
    }
}

@Composable
private fun SlideToStartButton(onCompleted: () -> Unit) {
    var dragOffsetPx by remember { mutableFloatStateOf(0f) }
    var maxTravelPx by remember { mutableFloatStateOf(0f) }
    var completed by remember { mutableStateOf(false) }

    val knobSize = 44.dp
    val trackPadding = 6.dp
    val density = androidx.compose.ui.platform.LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(AppPanelSoft)
            .onSizeChanged { layoutSize ->
                val knobPx = with(density) { knobSize.toPx() }
                val padPx = with(density) { trackPadding.toPx() }
                maxTravelPx = (layoutSize.width - knobPx - (padPx * 2f)).coerceAtLeast(0f)
                dragOffsetPx = dragOffsetPx.coerceIn(0f, maxTravelPx)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Get Started",
            color = Color(0xFFD5D5D8),
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
            modifier = Modifier.align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .padding(start = trackPadding)
                .offset { IntOffset(dragOffsetPx.roundToInt(), 0) }
                .size(knobSize)
                .clip(CircleShape)
                .background(AppOrange)
                .pointerInput(maxTravelPx, completed) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, dragAmount ->
                            if (completed) return@detectHorizontalDragGestures
                            dragOffsetPx = (dragOffsetPx + dragAmount).coerceIn(0f, maxTravelPx)
                        },
                        onDragEnd = {
                            if (completed) return@detectHorizontalDragGestures
                            if (dragOffsetPx >= maxTravelPx * 0.85f) {
                                completed = true
                                dragOffsetPx = maxTravelPx
                                onCompleted()
                            } else {
                                dragOffsetPx = 0f
                            }
                        },
                        onDragCancel = {
                            if (!completed) dragOffsetPx = 0f
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = AppWhite)
        }
    }
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
                    text = "Complete\nyour today's\ntasks!",
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

private fun detailHeadline(title: String): String {
    val firstSpace = title.indexOf(' ')
    return if (firstSpace == -1) title else title.substring(0, firstSpace) + "\n" + title.substring(firstSpace + 1)
}

@Preview(showBackground = true)
@Composable
private fun IntroScreenPreview() {
    TodoAppTheme { IntroScreen(onGetStarted = {}) }
}
