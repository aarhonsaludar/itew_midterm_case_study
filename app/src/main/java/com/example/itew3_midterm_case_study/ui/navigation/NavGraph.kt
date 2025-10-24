package com.example.itew3_midterm_case_study.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.itew3_midterm_case_study.ui.screens.*
import com.example.itew3_midterm_case_study.ui.viewmodel.AttendanceViewModel
import com.example.itew3_midterm_case_study.ui.viewmodel.ClassViewModel
import com.example.itew3_midterm_case_study.ui.viewmodel.StudentViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ClassList : Screen("class_list")
    object StudentList : Screen("student_list/{classId}/{className}") {
        fun createRoute(classId: Int, className: String) = "student_list/$classId/$className"
    }
    object AttendanceMarking : Screen("attendance_marking/{classId}/{className}") {
        fun createRoute(classId: Int, className: String) = "attendance_marking/$classId/$className"
    }
    object Reports : Screen("reports/{classId}/{className}") {
        fun createRoute(classId: Int, className: String) = "reports/$classId/$className"
    }
    object OverallReports : Screen("overall_reports")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    classViewModel: ClassViewModel,
    studentViewModel: StudentViewModel,
    attendanceViewModel: AttendanceViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                classViewModel = classViewModel,
                studentViewModel = studentViewModel,
                onNavigateToClasses = {
                    navController.navigate(Screen.ClassList.route)
                },
                onNavigateToStudents = { classId, className ->
                    navController.navigate(Screen.StudentList.createRoute(classId, className))
                },
                onNavigateToMarkAttendance = { classId, className ->
                    navController.navigate(Screen.AttendanceMarking.createRoute(classId, className))
                },
                onNavigateToOverallReports = {
                    navController.navigate(Screen.OverallReports.route)
                }
            )
        }

        composable(Screen.ClassList.route) {
            ClassListScreen(
                viewModel = classViewModel,
                onClassClick = { classId ->
                    // Get class name and navigate
                    val className = classViewModel.allClasses.value.find { it.id == classId }?.className ?: ""
                    navController.navigate(Screen.StudentList.createRoute(classId, className))
                },
                onTakeAttendance = {
                    // Show class selection for attendance
                    // For simplicity, we'll navigate to first class if available
                    val firstClass = classViewModel.allClasses.value.firstOrNull()
                    firstClass?.let {
                        navController.navigate(Screen.AttendanceMarking.createRoute(it.id, it.className))
                    }
                }
            )
        }

        composable(
            route = Screen.StudentList.route,
            arguments = listOf(
                navArgument("classId") { type = NavType.IntType },
                navArgument("className") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val classId = backStackEntry.arguments?.getInt("classId") ?: 0
            val className = backStackEntry.arguments?.getString("className") ?: ""

            StudentListScreen(
                classId = classId,
                className = className,
                viewModel = studentViewModel,
                onNavigateBack = { navController.navigateUp() },
                onMarkAttendance = { cId ->
                    navController.navigate(Screen.AttendanceMarking.createRoute(cId, className))
                }
            )
        }

        composable(
            route = Screen.AttendanceMarking.route,
            arguments = listOf(
                navArgument("classId") { type = NavType.IntType },
                navArgument("className") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val classId = backStackEntry.arguments?.getInt("classId") ?: 0
            val className = backStackEntry.arguments?.getString("className") ?: ""

            AttendanceMarkingScreen(
                classId = classId,
                className = className,
                studentViewModel = studentViewModel,
                attendanceViewModel = attendanceViewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = Screen.Reports.route,
            arguments = listOf(
                navArgument("classId") { type = NavType.IntType },
                navArgument("className") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val classId = backStackEntry.arguments?.getInt("classId") ?: 0
            val className = backStackEntry.arguments?.getString("className") ?: ""

            ReportsScreen(
                classId = classId,
                className = className,
                studentViewModel = studentViewModel,
                attendanceViewModel = attendanceViewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.OverallReports.route) {
            OverallReportsScreen(
                classViewModel = classViewModel,
                studentViewModel = studentViewModel,
                attendanceViewModel = attendanceViewModel,
                onNavigateBack = { navController.navigateUp() },
                onClassClick = { classId, className ->
                    navController.navigate(Screen.Reports.createRoute(classId, className))
                }
            )
        }
    }
}