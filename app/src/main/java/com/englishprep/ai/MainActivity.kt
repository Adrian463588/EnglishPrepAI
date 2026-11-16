package com.englishprep.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.englishprep.ai.data.datastore.ThemeMode
import com.englishprep.ai.data.datastore.UserPreferencesRepository
import com.englishprep.ai.data.datastore.UserSettings
import com.englishprep.ai.ui.feature.chat.ChatRoute
import com.englishprep.ai.ui.feature.exam.ExamRoute
import com.englishprep.ai.ui.feature.home.HomeRoute
import com.englishprep.ai.ui.feature.learn.LearnRoute
import com.englishprep.ai.ui.feature.learn.ModuleDetailRoute
import com.englishprep.ai.ui.feature.practice.PracticeRoute
import com.englishprep.ai.ui.feature.profile.ProfileRoute
import com.englishprep.ai.ui.feature.progress.ProgressRoute
import com.englishprep.ai.ui.feature.results.ResultsRoute
import com.englishprep.ai.ui.navigation.AppRoute
import com.englishprep.ai.ui.navigation.TopLevelDestination
import com.englishprep.ai.ui.theme.EnglishPrepTheme
import com.englishprep.ai.ui.theme.LocalSemanticStatusColors
import com.englishprep.ai.ui.theme.MotionTokens
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by preferencesRepository.settingsFlow.collectAsStateWithLifecycle(
                initialValue = UserSettings(),
            )
            val useDark = when (settings.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            val currentDensity = LocalDensity.current
            val effectiveDensity = if (settings.fontScaleBoost) {
                Density(currentDensity.density, (currentDensity.fontScale * 1.25f).coerceAtMost(2.0f))
            } else {
                currentDensity
            }

            CompositionLocalProvider(LocalDensity provides effectiveDensity) {
                EnglishPrepTheme(
                    darkTheme = useDark,
                    reduceMotion = settings.reduceMotion,
                    hapticsEnabled = settings.hapticsEnabled,
                ) {
                    EnglishPrepRootApp()
                }
            }
        }
    }
}

@Composable
fun EnglishPrepRootApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val useRail = LocalConfiguration.current.screenWidthDp >= 600

    val showTopLevelNav = TopLevelDestination.entries.any { dest ->
        currentDestination?.hasRoute(dest.route::class) == true
    }

    val navigateToTopLevel: (TopLevelDestination) -> Unit = { dest ->
        navController.navigate(dest.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (showTopLevelNav && !useRail) {
                NavigationBar {
                    TopLevelDestination.entries.forEach { dest ->
                        val selected = currentDestination?.hasRoute(dest.route::class) == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navigateToTopLevel(dest) },
                            icon = { Icon(dest.icon, contentDescription = null) },
                            label = { Text(dest.label) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (showTopLevelNav && useRail) {
                NavigationRail {
                    TopLevelDestination.entries.forEach { dest ->
                        val selected = currentDestination?.hasRoute(dest.route::class) == true
                        NavigationRailItem(
                            selected = selected,
                            onClick = { navigateToTopLevel(dest) },
                            icon = { Icon(dest.icon, contentDescription = null) },
                            label = { Text(dest.label) },
                        )
                    }
                }
            }
            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                val reduceMotion = LocalSemanticStatusColors.current.reduceMotion
                val transMs = if (reduceMotion) 0 else MotionTokens.ScreenTransitionMs
                NavHost(
                    navController = navController,
                    startDestination = AppRoute.Home,
                    enterTransition = {
                        if (reduceMotion) {
                            EnterTransition.None
                        } else {
                            fadeIn(tween(transMs)) + slideInHorizontally(tween(transMs)) { it / 14 }
                        }
                    },
                    exitTransition = {
                        if (reduceMotion) ExitTransition.None else fadeOut(tween(transMs))
                    },
                    popEnterTransition = {
                        if (reduceMotion) {
                            EnterTransition.None
                        } else {
                            fadeIn(tween(transMs)) + slideInHorizontally(tween(transMs)) { -it / 14 }
                        }
                    },
                    popExitTransition = {
                        if (reduceMotion) {
                            ExitTransition.None
                        } else {
                            fadeOut(tween(transMs)) + slideOutHorizontally(tween(transMs)) { it / 14 }
                        }
                    },
                ) {
                    composable<AppRoute.Home> {
                        HomeRoute(
                            onOpenSession = { sid -> navController.navigate(AppRoute.Exam(sid)) },
                            onOpenModule = { mid -> navController.navigate(AppRoute.ModuleDetail(mid)) },
                            onOpenPractice = { navigateToTopLevel(TopLevelDestination.PRACTICE) },
                            onOpenChat = { src -> navController.navigate(AppRoute.Chat(src)) },
                            onOpenProfileAi = { navigateToTopLevel(TopLevelDestination.PROFILE) },
                        )
                    }
                    composable<AppRoute.Learn> {
                        LearnRoute(
                            onOpenModule = { mid -> navController.navigate(AppRoute.ModuleDetail(mid)) },
                        )
                    }
                    composable<AppRoute.ModuleDetail> {
                        ModuleDetailRoute(
                            onBack = { navController.popBackStack() },
                            onAskTutor = { mid -> navController.navigate(AppRoute.Chat(mid)) },
                        )
                    }
                    composable<AppRoute.Practice> {
                        PracticeRoute(
                            onOpenSession = { sid -> navController.navigate(AppRoute.Exam(sid)) },
                        )
                    }
                    composable<AppRoute.Exam> {
                        ExamRoute(
                            onBack = { navController.popBackStack() },
                            onCompleted = { sid ->
                                navController.navigate(AppRoute.Results(sid)) {
                                    popUpTo<AppRoute.Exam> { inclusive = true }
                                }
                            },
                        )
                    }
                    composable<AppRoute.Results> {
                        ResultsRoute(
                            onBackHome = { navigateToTopLevel(TopLevelDestination.HOME) },
                            onOpenModule = { mid -> navController.navigate(AppRoute.ModuleDetail(mid)) },
                            onOpenRetrySession = { sid ->
                                navController.navigate(AppRoute.Exam(sid)) {
                                    popUpTo<AppRoute.Results> { inclusive = true }
                                }
                            },
                        )
                    }
                    composable<AppRoute.Chat> {
                        ChatRoute(
                            onBack = { navController.popBackStack() },
                            onOpenProfileAi = { navigateToTopLevel(TopLevelDestination.PROFILE) },
                        )
                    }
                    composable<AppRoute.Progress> {
                        ProgressRoute(
                            onOpenResults = { sid -> navController.navigate(AppRoute.Results(sid)) },
                            onOpenModule = { mid -> navController.navigate(AppRoute.ModuleDetail(mid)) },
                            onOpenPractice = { navigateToTopLevel(TopLevelDestination.PRACTICE) },
                        )
                    }
                    composable<AppRoute.Profile> {
                        ProfileRoute()
                    }
                }
            }
        }
    }
}
