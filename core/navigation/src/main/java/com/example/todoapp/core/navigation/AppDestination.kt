package com.example.todoapp.core.navigation

sealed interface AppDestination {
    val route: String
}

data object IntroDestination : AppDestination {
    override val route: String = "intro"
}

data object DashboardDestination : AppDestination {
    override val route: String = "dashboard"
}

data object DetailDestination : AppDestination {
    const val ARG_PROJECT_ID: String = "projectId"
    override val route: String = "detail/{$ARG_PROJECT_ID}"

    fun createRoute(projectId: String): String = "detail/$projectId"
}
