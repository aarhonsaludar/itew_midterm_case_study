package com.example.itew3_midterm_case_study.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.itew3_midterm_case_study.data.entities.StudentEntity
import com.example.itew3_midterm_case_study.ui.viewmodel.StudentViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(
    classId: Int,
    className: String,
    viewModel: StudentViewModel,
    onNavigateBack: () -> Unit,
    onMarkAttendance: (Int) -> Unit
) {
    val students by viewModel.getStudentsByClass(classId).collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(className) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onMarkAttendance(classId) }) {
                        Icon(Icons.Default.CheckCircle, "Mark Attendance")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, "Add Student")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(students) { student ->
                StudentCard(
                    student = student,
                    onDelete = { viewModel.deleteStudent(student) }
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
                        Text("No students added yet")
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddStudentDialog(
            classId = classId,
            viewModel = viewModel,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun StudentCard(
    student: StudentEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color(0xFFFFF5E4) // Cream
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Student Name indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = androidx.compose.ui.graphics.Color(0xFFFF9494), // CoralPink
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Student Name:",
                            style = MaterialTheme.typography.labelSmall,
                            color = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = student.studentName,
                        style = MaterialTheme.typography.titleMedium,
                        color = androidx.compose.ui.graphics.Color(0xFF3A3530)
                    )
                }

                // Student ID indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = androidx.compose.ui.graphics.Color(0xFFFFD1D1), // MediumPink
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Student ID:",
                            style = MaterialTheme.typography.labelSmall,
                            color = androidx.compose.ui.graphics.Color(0xFF5A4040),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = student.studentIdNumber,
                        style = MaterialTheme.typography.bodyMedium,
                        color = androidx.compose.ui.graphics.Color(0xFF5A4040)
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    "Delete",
                    tint = androidx.compose.ui.graphics.Color(0xFFFF5252)
                )
            }
        }
    }
}

@Composable
fun AddStudentDialog(
    classId: Int,
    viewModel: StudentViewModel,
    onDismiss: () -> Unit
) {
    var studentName by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Student") },
        text = {
            Column {
                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    label = { Text("Student Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = studentId,
                    onValueChange = { studentId = it },
                    label = { Text("Student ID Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (error.isNotEmpty()) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                scope.launch {
                    when {
                        studentName.isBlank() -> error = "Student name is required"
                        studentId.isBlank() -> error = "Student ID is required"
                        viewModel.checkStudentIdExists(studentId.trim(), classId) -> {
                            error = "Student ID already exists in this class"
                        }
                        else -> {
                            viewModel.addStudent(studentName.trim(), studentId.trim(), classId)
                            onDismiss()
                        }
                    }
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
