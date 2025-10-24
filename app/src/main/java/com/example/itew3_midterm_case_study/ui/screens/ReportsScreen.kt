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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch
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
    var stats by remember { mutableStateOf<com.example.itew3_midterm_case_study.ui.viewmodel.AttendanceStats?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(student.id, startDate, endDate) {
        scope.launch {
            stats = if (startDate != null && endDate != null) {
                attendanceViewModel.calculateAttendanceStatsByDateRange(student.id, startDate, endDate)
            } else {
                attendanceViewModel.calculateAttendanceStats(student.id)
            }
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Filter by Date Range",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Start Date Picker
            DatePickerField(
                label = "Start Date",
                selectedDate = startDate,
                onDateSelected = onStartDateChange
            )

            // End Date Picker
            DatePickerField(
                label = "End Date",
                selectedDate = endDate,
                onDateSelected = onEndDateChange
            )

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
                    Text("Apply Filter")
                }
            }
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }

    OutlinedButton(
        onClick = { showDatePicker = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = selectedDate?.format(dateFormatter) ?: "Select Date",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Icon(Icons.Default.CalendarToday, contentDescription = null)
        }
    }

    if (showDatePicker) {
        SimpleDatePickerDialog(
            onDateSelected = { date ->
                onDateSelected(date)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun SimpleDatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedYear by remember { mutableIntStateOf(LocalDate.now().year) }
    var selectedMonth by remember { mutableIntStateOf(LocalDate.now().monthValue) }
    var selectedDay by remember { mutableIntStateOf(LocalDate.now().dayOfMonth) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Date") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Year Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Year:")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedButton(onClick = { selectedYear-- }) { Text("-") }
                        Text(
                            text = selectedYear.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        OutlinedButton(onClick = { selectedYear++ }) { Text("+") }
                    }
                }

                // Month Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Month:")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedButton(onClick = {
                            selectedMonth = if (selectedMonth > 1) selectedMonth - 1 else 12
                        }) { Text("-") }
                        Text(
                            text = selectedMonth.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        OutlinedButton(onClick = {
                            selectedMonth = if (selectedMonth < 12) selectedMonth + 1 else 1
                        }) { Text("+") }
                    }
                }

                // Day Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Day:")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedButton(onClick = {
                            selectedDay = if (selectedDay > 1) selectedDay - 1 else 31
                        }) { Text("-") }
                        Text(
                            text = selectedDay.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        OutlinedButton(onClick = {
                            selectedDay = if (selectedDay < 31) selectedDay + 1 else 1
                        }) { Text("+") }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                try {
                    val date = LocalDate.of(selectedYear, selectedMonth, selectedDay)
                    onDateSelected(date)
                } catch (_: Exception) {
                    // Invalid date, do nothing
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ActiveFilterChip(
    startDate: LocalDate,
    endDate: LocalDate,
    onClear: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }

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
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Column {
                    Text(
                        text = "Filtered by Date Range",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${startDate.format(dateFormatter)} - ${endDate.format(dateFormatter)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            TextButton(onClick = onClear) {
                Text("Clear")
            }
        }
    }
}

