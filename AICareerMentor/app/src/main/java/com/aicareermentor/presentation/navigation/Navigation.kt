package com.aicareermentor.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aicareermentor.presentation.screens.history.HistoryScreen
import com.aicareermentor.presentation.screens.home.HomeScreen
import com.aicareermentor.presentation.screens.interview.InterviewScreen
import com.aicareermentor.presentation.screens.resume.ResumeAnalyzerScreen
import com.aicareermentor.presentation.screens.roadmap.CareerRoadmapScreen
import com.aicareermentor.presentation.screens.skillgap.SkillGapScreen

sealed class Screen(val route: String) {
    object Home           : Screen("home")
    object ResumeAnalyzer : Screen("resume_analyzer")
    object SkillGap       : Screen("skill_gap")
    object Interview      : Screen("interview")
    object CareerRoadmap  : Screen("career_roadmap")
    object History        : Screen("history")
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onResume    = { navController.navigate(Screen.ResumeAnalyzer.route) },
                onSkillGap  = { navController.navigate(Screen.SkillGap.route) },
                onInterview = { navController.navigate(Screen.Interview.route) },
                onRoadmap   = { navController.navigate(Screen.CareerRoadmap.route) },
                onHistory   = { navController.navigate(Screen.History.route) }
            )
        }
        composable(Screen.ResumeAnalyzer.route) { ResumeAnalyzerScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.SkillGap.route)       { SkillGapScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Interview.route)      { InterviewScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.CareerRoadmap.route)  { CareerRoadmapScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.History.route)        { HistoryScreen(onBack = { navController.popBackStack() }) }
    }
}
