package com.example.itew3_midterm_case_study.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.itew3_midterm_case_study.data.entities.StudentEntity
import com.example.itew3_midterm_case_study.ui.viewmodel.AttendanceStats
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

    // Search and filter states
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    // Undo/Redo functionality
    val undoStack = remember { mutableStateListOf<Map<Int, String>>() }
    val redoStack = remember { mutableStateListOf<Map<Int, String>>() }

    fun saveStateForUndo() {
        undoStack.add(attendanceMap.toMap())
        if (undoStack.size > 50) undoStack.removeAt(0) // Keep max 50 states
        redoStack.clear()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.add(attendanceMap.toMap())
            val previousState = undoStack.removeAt(undoStack.lastIndex)
            attendanceMap.clear()
            attendanceMap.putAll(previousState)
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.add(attendanceMap.toMap())
            val nextState = redoStack.removeAt(redoStack.lastIndex)
            attendanceMap.clear()
            attendanceMap.putAll(nextState)
        }
    }

    // Filtered students based on search query and filter
    val filteredStudents = remember(students, searchQuery, selectedFilter, attendanceMap) {
        students.filter { student ->
            // Search filter
            val matchesSearch = searchQuery.isEmpty() ||
                student.studentName.contains(searchQuery, ignoreCase = true) ||
                student.studentIdNumber.contains(searchQuery, ignoreCase = true)

            // Status filter
            val matchesStatus = when (selectedFilter) {
                "All" -> true
                "Present" -> attendanceMap[student.id] == "Present"
                "Absent" -> attendanceMap[student.id] == "Absent"
                "Late" -> attendanceMap[student.id] == "Late"
                "Unmarked" -> !attendanceMap.containsKey(student.id) || attendanceMap[student.id] == null
                else -> true
            }

            matchesSearch && matchesStatus
        }
    }

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
                },
                actions = {
                    IconButton(
                        onClick = { undo() },
                        enabled = undoStack.isNotEmpty()
                    ) {
                        Icon(
                            Icons.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if (undoStack.isNotEmpty())
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                    IconButton(
                        onClick = { redo() },
                        enabled = redoStack.isNotEmpty()
                    ) {
                        Icon(
                            Icons.Filled.Redo,
                            contentDescription = "Redo",
                            tint = if (redoStack.isNotEmpty())
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
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
                enabled = attendanceMap.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9494), // CoralPink
                    disabledContainerColor = Color(0xFFFFD1D1) // MediumPink when disabled
                )
            ) {
                Text("Save Attendance", style = MaterialTheme.typography.titleMedium)
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
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFE3E1) // LightPink
                )
            ) {
                Text(
                    text = "Date: ${selectedDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF3A3530)
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search by name or ID") },

                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search students")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                        }
                    }
                },



                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF9494),
                    unfocusedBorderColor = Color(0xFFFFD1D1),
                    cursorColor = Color(0xFFFF9494)
                )
            )

            // Filter chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("All", "Present", "Absent", "Late", "Unmarked")
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) }
                    )
                }
            }

            // Summary text
            if (students.isNotEmpty()) {
                Text(
                    text = "Showing ${filteredStudents.size} of ${students.size} students",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredStudents, key = { it.id }) { student ->
                    var attendanceStats by remember { mutableStateOf<AttendanceStats?>(null) }

                    LaunchedEffect(student.id) {
                        attendanceStats = attendanceViewModel.calculateAttendanceStats(student.id)
                    }

                    SwipeableAttendanceRow(
                        student = student,
                        currentStatus = attendanceMap[student.id],
                        attendanceStats = attendanceStats,
                        onStatusChange = { status ->
                            saveStateForUndo()
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
                } else if (filteredStudents.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No students match your search or filter")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SwipeableAttendanceRow(
    student: StudentEntity,
    currentStatus: String?,
    attendanceStats: AttendanceStats?,
    onStatusChange: (String) -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Determine card color based on attendance pattern
    val cardColor = when {
        currentStatus == "Present" -> Color(0xFFE8F5E9) // Light green
        currentStatus == "Absent" -> Color(0xFFFFEBEE) // Light red
        currentStatus == "Late" -> Color(0xFFFFF3E0) // Light orange
        attendanceStats != null && attendanceStats.percentage < 75f -> Color(0xFFFFD1D1) // MediumPink for at-risk
        attendanceStats != null && attendanceStats.percentage >= 90f -> Color(0xFFFFE3E1) // LightPink for excellent
        else -> Color(0xFFFFF5E4) // Cream default
    }

    val animatedCardColor by animateColorAsState(
        targetValue = cardColor,
        label = "cardColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (isDragging) 0.95f else 1f,
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = {
                            isDragging = false
                            // Swipe right -> Present (threshold: 100px)
                            if (offsetX > 100f) {
                                onStatusChange("Present")
                            }
                            // Swipe left -> Absent (threshold: -100px)
                            else if (offsetX < -100f) {
                                onStatusChange("Absent")
                            }
                            offsetX = 0f
                        },
                        onDragCancel = {
                            isDragging = false
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            offsetX += dragAmount
                        }
                    )
                },
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = animatedCardColor
            )
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
                    // Show attendance percentage if available
                    attendanceStats?.let { stats ->
                        Text(
                            text = "Overall: ${"%.1f".format(stats.percentage)}% (${stats.present}/${stats.total})",
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                stats.percentage < 75f -> Color(0xFFFF5252)
                                stats.percentage >= 90f -> Color(0xFF4CAF50)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
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

        // Swipe indicator overlay
        if (isDragging && offsetX != 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        when {
                            offsetX > 100f -> Color(0xFF4CAF50).copy(alpha = 0.3f)
                            offsetX < -100f -> Color(0xFFF44336).copy(alpha = 0.3f)
                            else -> Color.Transparent
                        }
                    ),
                contentAlignment = if (offsetX > 0) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                if (offsetX > 100f || offsetX < -100f) {
                    Text(
                        text = if (offsetX > 0) "→ Present" else "Absent ←",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
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
            containerColor = if (isSelected) color else Color(0xFFFFD1D1), // MediumPink when unselected
            contentColor = Color.White
        ),
        modifier = Modifier.size(52.dp),
        contentPadding = PaddingValues(0.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
        )
    }
}
