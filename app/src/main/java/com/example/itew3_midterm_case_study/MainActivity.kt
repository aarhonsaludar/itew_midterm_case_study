package com.example.itew3_midterm_case_study

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.itew3_midterm_case_study.data.database.AttendanceDatabase
import com.example.itew3_midterm_case_study.data.repository.AttendanceRepository
import com.example.itew3_midterm_case_study.data.repository.ClassRepository
import com.example.itew3_midterm_case_study.data.repository.StudentRepository
import com.example.itew3_midterm_case_study.ui.navigation.NavGraph
import com.example.itew3_midterm_case_study.ui.theme.AttendanceTrackerTheme
import com.example.itew3_midterm_case_study.ui.viewmodel.AttendanceViewModel
import com.example.itew3_midterm_case_study.ui.viewmodel.ClassViewModel
import com.example.itew3_midterm_case_study.ui.viewmodel.StudentViewModel
import com.jakewharton.threetenabp.AndroidThreeTen

// main entry point of the attendance tracking application
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initialize date/time library for proper date handling
        AndroidThreeTen.init(this)

        // set up database and repository instances
        val database = AttendanceDatabase.getDatabase(applicationContext)
        val classRepository = ClassRepository(database.classDao())
        val studentRepository = StudentRepository(database.studentDao())
        val attendanceRepository = AttendanceRepository(database.attendanceDao())

        // create viewmodels that manage UI state and business logic
        val classViewModel = ClassViewModel(classRepository)
        val studentViewModel = StudentViewModel(studentRepository)
        val attendanceViewModel = AttendanceViewModel(attendanceRepository)

        setContent {
            AttendanceTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // set up navigation controller for screen navigation
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        classViewModel = classViewModel,
                        studentViewModel = studentViewModel,
                        attendanceViewModel = attendanceViewModel
                    )
                }
            }
        }
    }
}