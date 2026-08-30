package com.jobradar.app.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jobradar.app.presentation.ai.AiAssistantScreen
import com.jobradar.app.presentation.detail.JobDetailScreen
import com.jobradar.app.presentation.favorites.FavoritesScreen
import com.jobradar.app.presentation.jobs.JobsScreen
import com.jobradar.app.presentation.profile.ProfileScreen
import com.jobradar.app.presentation.resume.ResumeScreen
import com.jobradar.app.presentation.radar.RadarScreen
import com.jobradar.app.presentation.ui.components.BottomNavBar
import com.jobradar.app.presentation.ui.components.BottomTab

/** Navigation route names. */
object Routes {
    const val RADAR = "radar"
    const val JOBS = "jobs"
    const val FAVORITES = "favorites"
    const val PROFILE = "profile"
    const val JOB_DETAIL = "job/{jobId}"
    const val AI_ASSISTANT = "ai?q={q}"
    const val RESUME = "resume"

    fun jobDetail(jobId: Long) = "job/$jobId"
    fun aiAssistant(question: String? = null) = "ai?q=${question ?: ""}"
}

/** Map a route to its owning tab (for bottom nav highlight). */
private fun routeToTab(route: String?): BottomTab = when (route) {
    Routes.RADAR -> BottomTab.RADAR
    Routes.JOBS -> BottomTab.JOBS
    Routes.FAVORITES -> BottomTab.FAVORITES
    Routes.PROFILE -> BottomTab.PROFILE
    else -> BottomTab.RADAR
}

/**
 * Root navigation host.
 *
 * Single [NavHost] hosts the 4 tab destinations plus a pushed detail screen.
 * The bottom nav is shown on tab routes and hidden on the detail route. Tab
 * switches cross-fade; the detail push slides up from the bottom.
 */
@Composable
fun JobRadarNavHost(
    navController: NavHostController = rememberNavController(),
) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = currentRoute != Routes.JOB_DETAIL

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBottomBar) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    BottomNavBar(
                        current = routeToTab(currentRoute),
                        onSelect = { tab ->
                            val target = when (tab) {
                                BottomTab.RADAR -> Routes.RADAR
                                BottomTab.JOBS -> Routes.JOBS
                                BottomTab.FAVORITES -> Routes.FAVORITES
                                BottomTab.PROFILE -> Routes.PROFILE
                            }
                            if (currentRoute != target) {
                                navController.navigate(target) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
            color = Color.Transparent,
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.RADAR,
                modifier = Modifier.fillMaxSize(),
            ) {
                composable(Routes.RADAR) {
                    RadarScreen(
                        onNavigateToJob = { navController.navigate(Routes.jobDetail(it)) },
                        onNavigateToAi = { navController.navigate(Routes.aiAssistant()) },
                    )
                }
                composable(
                    Routes.JOBS,
                    enterTransition = { fadeIn(tween(240)) },
                    exitTransition = { fadeOut(tween(240)) },
                ) {
                    JobsScreen(onNavigateToJob = { navController.navigate(Routes.jobDetail(it)) })
                }
                composable(
                    Routes.FAVORITES,
                    enterTransition = { fadeIn(tween(240)) },
                    exitTransition = { fadeOut(tween(240)) },
                ) {
                    FavoritesScreen(onNavigateToJob = { navController.navigate(Routes.jobDetail(it)) })
                }
                composable(
                    Routes.PROFILE,
                    enterTransition = { fadeIn(tween(240)) },
                    exitTransition = { fadeOut(tween(240)) },
                ) {
                    ProfileScreen(onNavigateToResume = { navController.navigate(Routes.RESUME) })
                }
                composable(
                    route = Routes.JOB_DETAIL,
                    arguments = listOf(navArgument("jobId") { type = NavType.LongType }),
                    enterTransition = { slideInVertically(tween(300)) { it } + fadeIn(tween(300)) },
                    exitTransition = { fadeOut(tween(200)) },
                    popEnterTransition = { fadeIn(tween(200)) },
                    popExitTransition = { slideOutVertically(tween(300)) { it } + fadeOut(tween(300)) },
                ) {
                    JobDetailScreen(
                        onBack = { navController.popBackStack() },
                        onAnalyzeWithAi = { q -> navController.navigate(Routes.aiAssistant(q)) },
                    )
                }
                composable(
                    Routes.AI_ASSISTANT,
                    arguments = listOf(navArgument("q") { type = NavType.StringType; nullable = true; defaultValue = null }),
                    enterTransition = { fadeIn(tween(240)) },
                    exitTransition = { fadeOut(tween(200)) },
                    popExitTransition = { fadeOut(tween(200)) },
                ) { backStackEntry ->
                    val q = backStackEntry.arguments?.getString("q")
                    AiAssistantScreen(initialQuestion = q)
                }
                composable(
                    Routes.RESUME,
                    enterTransition = { fadeIn(tween(240)) },
                    exitTransition = { fadeOut(tween(200)) },
                    popExitTransition = { fadeOut(tween(200)) },
                ) {
                    ResumeScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
