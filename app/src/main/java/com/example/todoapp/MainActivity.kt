package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.core.designsystem.theme.TodoAppTheme
import com.example.todoapp.core.navigation.DashboardDestination
import com.example.todoapp.core.navigation.DetailDestination
import com.example.todoapp.core.navigation.IntroDestination
import com.example.todoapp.feature.onboarding.onboardingScreen
import com.example.todoapp.feature.todo.detail.detailScreen
import com.example.todoapp.feature.todo.home.homeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = IntroDestination.route
                ) {
                    onboardingScreen(onGetStarted = { navController.navigate(DashboardDestination.route) })
                    homeScreen(onCardClick = { cardId -> navController.navigate(DetailDestination.createRoute(cardId)) })
                    detailScreen(onClose = { navController.popBackStack() })
                }
            }
        }
    }
}
