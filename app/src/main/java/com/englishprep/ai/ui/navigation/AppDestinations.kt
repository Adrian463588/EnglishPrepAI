package com.englishprep.ai.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable data object Home : AppRoute
    @Serializable data object Learn : AppRoute
    @Serializable data class ModuleDetail(val moduleId: String) : AppRoute
    @Serializable data object Practice : AppRoute
    @Serializable data class Exam(val sessionId: String) : AppRoute
    @Serializable data class Results(val sessionId: String) : AppRoute
    @Serializable data class Chat(val contextSourceId: String? = null) : AppRoute
    @Serializable data object Progress : AppRoute
    @Serializable data object Profile : AppRoute
}

enum class TopLevelDestination(
    val route: AppRoute,
    val label: String,
    val icon: ImageVector,
) {
    HOME(AppRoute.Home, "Beranda", Icons.Default.Home),
    LEARN(AppRoute.Learn, "Materi", Icons.AutoMirrored.Filled.MenuBook),
    PRACTICE(AppRoute.Practice, "Latihan", Icons.Default.Quiz),
    PROGRESS(AppRoute.Progress, "Progres", Icons.Default.Assessment),
    PROFILE(AppRoute.Profile, "Profil", Icons.Default.Person),
}
