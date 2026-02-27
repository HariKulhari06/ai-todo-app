package com.example.todoapp.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.todoapp.core.designsystem.theme.AppBlack
import com.example.todoapp.core.designsystem.theme.AppGray
import com.example.todoapp.core.designsystem.theme.AppOrange
import com.example.todoapp.core.designsystem.theme.AppPanelSoft
import com.example.todoapp.core.designsystem.theme.AppWhite
import com.example.todoapp.core.designsystem.theme.TodoAppTheme
import com.example.todoapp.core.navigation.IntroDestination
import kotlin.math.roundToInt

fun NavGraphBuilder.onboardingScreen(onGetStarted: () -> Unit) {
    composable(route = IntroDestination.route) {
        IntroScreen(onGetStarted = onGetStarted)
    }
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
                text = "Easily manage your to-do list and take control of your schedule",
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

@Preview(showBackground = true)
@Composable
private fun IntroScreenPreview() {
    TodoAppTheme { IntroScreen(onGetStarted = {}) }
}
