package com.example.itew3_midterm_case_study.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.itew3_midterm_case_study.ui.viewmodel.ClassViewModel
import com.example.itew3_midterm_case_study.ui.viewmodel.StudentViewModel
import kotlinx.coroutines.flow.firstOrNull

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    classViewModel: ClassViewModel,
    studentViewModel: StudentViewModel,
    onNavigateToClasses: () -> Unit,
    onNavigateToStudents: (Int, String) -> Unit,
    onNavigateToMarkAttendance: (Int, String) -> Unit,
    onNavigateToOverallReports: () -> Unit
) {
    val classes by classViewModel.allClasses.collectAsState()
    var totalStudents by remember { mutableIntStateOf(0) }
    var showClassSelectionDialog by remember { mutableStateOf(false) }
    var selectedAction by remember { mutableStateOf<String?>(null) }

    // Calculate total students
    LaunchedEffect(classes) {
        var count = 0
        classes.forEach { classEntity ->
            val students = studentViewModel.getStudentsByClass(classEntity.id).firstOrNull()
            count += students?.size ?: 0
        }
        totalStudents = count
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Tracker") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Quick Statistics Section
            Text(
                text = "Quick Statistics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatisticCard(
                    title = "Classes",
                    count = classes.size,
                    icon = Icons.Default.Class,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                StatisticCard(
                    title = "Students",
                    count = totalStudents,
                    icon = Icons.Default.People,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }

            // Quick Actions Section
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            val quickActions = remember(classes) {
                buildList {
                    add(
                        QuickAction(
                            title = "Manage Classes",
                            icon = Icons.Default.Class,
                            color = Color(0xFF6200EE),
                            onClick = onNavigateToClasses
                        )
                    )

                    if (classes.isNotEmpty()) {
                        add(
                            QuickAction(
                                title = "View Students",
                                icon = Icons.Default.People,
                                color = Color(0xFF03DAC5),
                                onClick = {
                                    selectedAction = "students"
                                    showClassSelectionDialog = true
                                }
                            )
                        )
                        add(
                            QuickAction(
                                title = "Mark Attendance",
                                icon = Icons.Default.CheckCircle,
                                color = Color(0xFF4CAF50),
                                onClick = {
                                    selectedAction = "attendance"
                                    showClassSelectionDialog = true
                                }
                            )
                        )
                    }

                    // View Reports is always available and doesn't need class selection
                    add(
                        QuickAction(
                            title = "View Reports",
                            icon = Icons.Default.Assessment,
                            color = Color(0xFFFF9800),
                            onClick = onNavigateToOverallReports
                        )
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickActions) { action ->
                    QuickActionCard(action = action)
                }
            }

            // Info message if no classes
            if (classes.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Get started by creating your first class!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }

    // Class Selection Dialog
    if (showClassSelectionDialog) {
        ClassSelectionDialog(
            classes = classes,
            onDismiss = {
                showClassSelectionDialog = false
                selectedAction = null
            },
            onClassSelected = { classEntity ->
                showClassSelectionDialog = false
                when (selectedAction) {
                    "students" -> onNavigateToStudents(classEntity.id, classEntity.className)
                    "attendance" -> onNavigateToMarkAttendance(classEntity.id, classEntity.className)
                }
                selectedAction = null
            }
        )
    }
}

@Composable
fun StatisticCard(
    title: String,
    count: Int,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun QuickActionCard(action: QuickAction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = action.onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = action.color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                tint = action.color,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = action.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ClassSelectionDialog(
    classes: List<com.example.itew3_midterm_case_study.data.entities.ClassEntity>,
    onDismiss: () -> Unit,
    onClassSelected: (com.example.itew3_midterm_case_study.data.entities.ClassEntity) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select a Class") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                classes.forEach { classEntity ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onClassSelected(classEntity) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = classEntity.className,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = classEntity.subjectName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
