package com.example.itew3_midterm_case_study.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.itew3_midterm_case_study.data.entities.StudentEntity
import com.example.itew3_midterm_case_study.ui.viewmodel.AttendanceViewModel
import com.example.itew3_midterm_case_study.ui.viewmodel.StudentViewModel
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceMarkingScreen(
    classId: Int,
    className: String,
    studentViewModel: StudentViewModel,
    attendanceViewModel: AttendanceViewModel,
    onNavigateBack: () -> Unit
) {
    val students by studentViewModel.getStudentsByClass(classId).collectAsState(initial = emptyList())
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val attendanceMap = remember { mutableStateMapOf<Int, String>() }
    val scope = rememberCoroutineScope()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Load existing attendance
    LaunchedEffect(selectedDate, students) {
        students.forEach { student ->
            val existing = attendanceViewModel.getAttendance(student.id, selectedDate.format(dateFormatter))
            if (existing != null) {
                attendanceMap[student.id] = existing.status
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mark Attendance - $className") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    scope.launch {
                        attendanceMap.forEach { (studentId, status) ->
                            attendanceViewModel.markAttendance(
                                studentId,
                                selectedDate.format(dateFormatter),
                                status
                            )
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = attendanceMap.isNotEmpty()
            ) {
                Text("Save Attendance")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Date display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Date: ${selectedDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(students) { student ->
                    AttendanceRow(
                        student = student,
                        currentStatus = attendanceMap[student.id],
                        onStatusChange = { status ->
                            attendanceMap[student.id] = status
                        }
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
fun AttendanceRow(
    student: StudentEntity,
    currentStatus: String?,
    onStatusChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.studentName,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = student.studentIdNumber,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                AttendanceButton(
                    text = "P",
                    color = Color(0xFF4CAF50),
                    isSelected = currentStatus == "Present",
                    onClick = { onStatusChange("Present") }
                )
                AttendanceButton(
                    text = "A",
                    color = Color(0xFFF44336),
                    isSelected = currentStatus == "Absent",
                    onClick = { onStatusChange("Absent") }
                )
                AttendanceButton(
                    text = "L",
                    color = Color(0xFFFF9800),
                    isSelected = currentStatus == "Late",
                    onClick = { onStatusChange("Late") }
                )
            }
        }
    }
}

@Composable
fun AttendanceButton(
    text: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color else Color.LightGray,
            contentColor = Color.White
        ),
        modifier = Modifier.size(48.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}
