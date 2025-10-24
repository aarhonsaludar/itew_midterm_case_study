package com.example.itew3_midterm_case_study.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.itew3_midterm_case_study.data.entities.ClassEntity
import com.example.itew3_midterm_case_study.ui.viewmodel.AttendanceViewModel
import com.example.itew3_midterm_case_study.ui.viewmodel.ClassViewModel
import com.example.itew3_midterm_case_study.ui.viewmodel.StudentViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class ClassAttendanceStats(
    val classEntity: ClassEntity,
    val totalStudents: Int,
    val averageAttendance: Float,
    val totalPresent: Int,
    val totalAbsent: Int,
    val totalLate: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverallReportsScreen(
    classViewModel: ClassViewModel,
    studentViewModel: StudentViewModel,
    attendanceViewModel: AttendanceViewModel,
    onNavigateBack: () -> Unit,
    onClassClick: (Int, String) -> Unit
) {
    val classes by classViewModel.allClasses.collectAsState()
    var classStats by remember { mutableStateOf<List<ClassAttendanceStats>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(classes) {
        scope.launch {
            val stats = mutableListOf<ClassAttendanceStats>()
            classes.forEach { classEntity ->
                val students = studentViewModel.getStudentsByClass(classEntity.id).firstOrNull() ?: emptyList()

                var totalPresent = 0
                var totalAbsent = 0
                var totalLate = 0
                var totalPercentage = 0f

                students.forEach { student ->
                    val attendanceStats = attendanceViewModel.calculateAttendanceStats(student.id)
                    totalPresent += attendanceStats.present
                    totalAbsent += attendanceStats.absent
                    totalLate += attendanceStats.late
                    totalPercentage += attendanceStats.percentage
                }

                val averageAttendance = if (students.isNotEmpty()) totalPercentage / students.size else 0f

                stats.add(
                    ClassAttendanceStats(
                        classEntity = classEntity,
                        totalStudents = students.size,
                        averageAttendance = averageAttendance,
                        totalPresent = totalPresent,
                        totalAbsent = totalAbsent,
                        totalLate = totalLate
                    )
                )
            }
            classStats = stats
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Overall Attendance Reports") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overall Summary
            item {
                OverallSummaryCard(classStats)
            }

            // Individual Class Reports
            item {
                Text(
                    text = "Class Reports",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }

            if (classStats.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp)
                        ) {
                            Text(
                                text = "No classes found. Create a class to get started!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            } else {
                items(classStats) { stats ->
                    ClassReportCard(
                        stats = stats,
                        onClick = { onClassClick(stats.classEntity.id, stats.classEntity.className) }
                    )
                }
            }
        }
    }
}

@Composable
fun OverallSummaryCard(classStats: List<ClassAttendanceStats>) {
    val totalClasses = classStats.size
    val totalStudents = classStats.sumOf { it.totalStudents }
    val averageAttendance = if (classStats.isNotEmpty()) {
        classStats.map { it.averageAttendance }.average().toFloat()
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Overall Summary",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem("Classes", totalClasses.toString(), MaterialTheme.colorScheme.primary)
                SummaryItem("Students", totalStudents.toString(), MaterialTheme.colorScheme.secondary)
                SummaryItem("Avg. Attendance", "${"%.1f".format(averageAttendance)}%",
                    when {
                        averageAttendance >= 75 -> Color(0xFF4CAF50)
                        averageAttendance >= 50 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                )
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ClassReportCard(
    stats: ClassAttendanceStats,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stats.classEntity.className,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stats.classEntity.subjectName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Students: ${stats.totalStudents}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { stats.averageAttendance / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = when {
                    stats.averageAttendance >= 75 -> Color(0xFF4CAF50)
                    stats.averageAttendance >= 50 -> Color(0xFFFF9800)
                    else -> Color(0xFFF44336)
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Average Attendance: ${"%.1f".format(stats.averageAttendance)}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatChip("Present: ${stats.totalPresent}", Color(0xFF4CAF50))
                StatChip("Absent: ${stats.totalAbsent}", Color(0xFFF44336))
                StatChip("Late: ${stats.totalLate}", Color(0xFFFF9800))
            }
        }
    }
}

