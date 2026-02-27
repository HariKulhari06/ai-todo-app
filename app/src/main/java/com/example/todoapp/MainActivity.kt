package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.core.designsystem.theme.TodoAppTheme
import com.example.todoapp.core.navigation.IntroDestination
import com.example.todoapp.feature.todo.todoScreen

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
                    todoScreen(navController)
                }
            }
        }
    }
}
