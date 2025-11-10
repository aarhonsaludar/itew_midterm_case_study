package com.example.itew3_midterm_case_study.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.itew3_midterm_case_study.R
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
                title = {
                    Text(
                        "Attendance Tracker",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Welcome Message Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF9494) // CoralPink
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Logo
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Image(
                            painter = painterResource(id = R.mipmap.logo),
                            contentDescription = "Attendance Tracker Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    // Welcome Text
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Welcome to",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "Student Attendance Tracker",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Course Code Badge
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = "Course code icon",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Course Code: ITEW-3",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

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
                    color = Color(0xFFFF9494), // CoralPink
                    modifier = Modifier.weight(1f)
                )

                StatisticCard(
                    title = "Students",
                    count = totalStudents,
                    icon = Icons.Default.People,
                    color = Color(0xFFFF9494), // CoralPink - Fixed UI consistency
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
                            color = Color(0xFFFF9494), // CoralPink
                            onClick = onNavigateToClasses
                        )
                    )

                    if (classes.isNotEmpty()) {
                        add(
                            QuickAction(
                                title = "View Students",
                                icon = Icons.Default.People,
                                color = Color(0xFFFFD1D1), // MediumPink
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
                                color = Color(0xFFFFE3E1), // LightPink
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
                            color = Color(0xFFFFF5E4), // Cream
                            onClick = onNavigateToOverallReports
                        )
                    )
                }
            }

            // Quick Actions Grid (using Column instead of LazyVerticalGrid)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                quickActions.chunked(2).forEach { rowActions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowActions.forEach { action ->
                            QuickActionCard(
                                action = action,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Add spacer if odd number of items in row
                        if (rowActions.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Info message if no classes with animation
            AnimatedVisibility(
                visible = classes.isEmpty(),
                enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
                exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Information icon",
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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        shape = MaterialTheme.shapes.large
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
                contentDescription = "$title icon",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun QuickActionCard(action: QuickAction, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = action.onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = action.color
        ),
        shape = MaterialTheme.shapes.large
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
                contentDescription = "${action.title} icon",
                tint = Color(0xFF5A4040), // Darker color for contrast
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = action.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF3A3530) // Dark text for readability
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
        title = {
            Text(
                "Select a Class",
                style = MaterialTheme.typography.titleLarge
            )
        },
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
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = MaterialTheme.shapes.medium
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
        },
        shape = MaterialTheme.shapes.large
    )
}

