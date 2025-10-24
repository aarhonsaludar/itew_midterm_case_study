package com.example.itew3_midterm_case_study.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.itew3_midterm_case_study.data.entities.StudentEntity
import com.example.itew3_midterm_case_study.ui.viewmodel.AttendanceViewModel
import com.example.itew3_midterm_case_study.ui.viewmodel.StudentViewModel
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Correct import
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    classId: Int,
    className: String,
    studentViewModel: StudentViewModel,
    attendanceViewModel: AttendanceViewModel,
    onNavigateBack: () -> Unit
) {
    val students by studentViewModel.getStudentsByClass(classId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Report - $className") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(students) { student ->
                StudentAttendanceCard(
                    student = student,
                    attendanceViewModel = attendanceViewModel
                )
            }
        }
    }
}

@Composable
fun StudentAttendanceCard(
    student: StudentEntity,
    attendanceViewModel: AttendanceViewModel
) {
    var stats by remember { mutableStateOf<com.example.itew3_midterm_case_study.ui.viewmodel.AttendanceStats?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(student.id) {
        scope.launch {
            stats = attendanceViewModel.calculateAttendanceStats(student.id)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = student.studentName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "ID: ${student.studentIdNumber}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            stats?.let { s ->
                // Attendance percentage
                LinearProgressIndicator(
                    progress = { s.percentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = when {
                        s.percentage >= 75 -> Color(0xFF4CAF50)
                        s.percentage >= 50 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    },
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Attendance: ${"%.1f".format(s.percentage)}%",
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatChip("Present: ${s.present}", Color(0xFF4CAF50))
                    StatChip("Absent: ${s.absent}", Color(0xFFF44336))
                    StatChip("Late: ${s.late}", Color(0xFFFF9800))
                }

                Text(
                    text = "Total Days: ${s.total}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun StatChip(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}