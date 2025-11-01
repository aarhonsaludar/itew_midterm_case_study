package com.example.itew3_midterm_case_study.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.itew3_midterm_case_study.data.entities.StudentEntity
import com.example.itew3_midterm_case_study.ui.viewmodel.AttendanceStats
import com.example.itew3_midterm_case_study.ui.viewmodel.AttendanceViewModel
import com.example.itew3_midterm_case_study.ui.viewmodel.StudentViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

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
    var showDateFilter by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var isFilterActive by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Report - $className") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDateFilter = !showDateFilter }) {
                        Icon(Icons.Default.FilterList, "Filter by Date")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Date Filter Section
            if (showDateFilter) {
                DateFilterCard(
                    startDate = startDate,
                    endDate = endDate,
                    onStartDateChange = { startDate = it },
                    onEndDateChange = { endDate = it },
                    onApplyFilter = {
                        isFilterActive = startDate != null && endDate != null
                        showDateFilter = false
                    },
                    onClearFilter = {
                        startDate = null
                        endDate = null
                        isFilterActive = false
                        showDateFilter = false
                    }
                )
            }

            // Active Filter Display
            if (isFilterActive && startDate != null && endDate != null) {
                ActiveFilterChip(
                    startDate = startDate!!,
                    endDate = endDate!!,
                    onClear = {
                        startDate = null
                        endDate = null
                        isFilterActive = false
                    }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(students) { student ->
                    StudentAttendanceCard(
                        student = student,
                        attendanceViewModel = attendanceViewModel,
                        startDate = if (isFilterActive) startDate?.format(dateFormatter) else null,
                        endDate = if (isFilterActive) endDate?.format(dateFormatter) else null
                    )
                }

                if (students.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No students in this class")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentAttendanceCard(
    student: StudentEntity,
    attendanceViewModel: AttendanceViewModel,
    startDate: String? = null,
    endDate: String? = null
) {
    var stats by remember { mutableStateOf<AttendanceStats?>(null) }

    LaunchedEffect(student.id, startDate, endDate) {
        stats = if (startDate != null && endDate != null) {
            attendanceViewModel.calculateAttendanceStatsByDateRange(student.id, startDate, endDate)
        } else {
            attendanceViewModel.calculateAttendanceStats(student.id)
        }
    }

    stats?.let { s ->
        // Color-coded card based on attendance percentage
        val cardColor = when {
            s.percentage >= 90f -> Color(0xFFFFE3E1) // LightPink for excellent
            s.percentage >= 75f -> Color(0xFFFFF5E4) // Cream for good
            s.percentage >= 50f -> Color(0xFFFFD1D1) // MediumPink for warning
            else -> Color(0xFFFF9494).copy(alpha = 0.3f) // CoralPink for critical
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardColor
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = student.studentName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3A3530)
                        )

                        Text(
                            text = student.studentIdNumber,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5A4040)
                        )
                    }

                    // Attendance percentage badge
                    Surface(
                        color = when {
                            s.percentage >= 90f -> Color(0xFF4CAF50)
                            s.percentage >= 75f -> Color(0xFFFF9800)
                            else -> Color(0xFFFF5252)
                        },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = "${"%.1f".format(s.percentage)}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats row with new palette
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatChip("Present: ${s.present}", Color(0xFF4CAF50))
                    StatChip("Absent: ${s.absent}", Color(0xFFFF5252))
                    StatChip("Late: ${s.late}", Color(0xFFFF9800))
                }

                // Total count
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Total Days: ${s.total}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5A4040),
                    fontWeight = FontWeight.Medium
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

@Composable
fun DateFilterCard(
    startDate: LocalDate?,
    endDate: LocalDate?,
    onStartDateChange: (LocalDate?) -> Unit,
    onEndDateChange: (LocalDate?) -> Unit,
    onApplyFilter: () -> Unit,
    onClearFilter: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Filter by Date Range",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Start Date: ${startDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: "Not selected"}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "End Date: ${endDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: "Not selected"}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Note: Date picker functionality requires additional implementation",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClearFilter,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }
                Button(
                    onClick = onApplyFilter,
                    modifier = Modifier.weight(1f),
                    enabled = startDate != null && endDate != null
                ) {
                    Text("Apply")
                }
            }
        }
    }
}

@Composable
fun ActiveFilterChip(
    startDate: LocalDate,
    endDate: LocalDate,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filtered: ${startDate.format(DateTimeFormatter.ofPattern("MMM dd"))} - ${endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            IconButton(onClick = onClear) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = "Clear filter",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
