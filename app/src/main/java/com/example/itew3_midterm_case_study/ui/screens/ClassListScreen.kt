package com.example.itew3_midterm_case_study.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.itew3_midterm_case_study.data.entities.ClassEntity
import com.example.itew3_midterm_case_study.ui.viewmodel.ClassViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassListScreen(
    viewModel: ClassViewModel,
    onClassClick: (Int) -> Unit,
    onTakeAttendance: () -> Unit
) {
    val classes by viewModel.allClasses.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Classes",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = onTakeAttendance) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Take Attendance"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Class"
                )
            }
        }
    ) { padding ->
        if (classes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Class,
                        contentDescription = "No classes icon",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "No classes yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap the + button to add your first class",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(classes, key = { it.id }) { classEntity ->
                    AnimatedVisibility(
                        visible = true,
                        enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
                        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
                    ) {
                        ClassCard(
                            classEntity = classEntity,
                            onClick = { onClassClick(classEntity.id) },
                            onDelete = { viewModel.deleteClass(classEntity) }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddClassDialog(
            onDismiss = { showDialog = false },
            onConfirm = { className, subjectName ->
                viewModel.addClass(className, subjectName)
                showDialog = false
            }
        )
    }
}

@Composable
fun ClassCard(
    classEntity: ClassEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color(0xFFFFF5E4) // Cream
        ),
        shape = MaterialTheme.shapes.large
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section Name indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = androidx.compose.ui.graphics.Color(0xFFFF9494), // CoralPink
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Section Name:",
                            style = MaterialTheme.typography.labelMedium,
                            color = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = classEntity.className,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = androidx.compose.ui.graphics.Color(0xFF3A3530)
                    )
                }

                // Course Subject indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = androidx.compose.ui.graphics.Color(0xFFFFD1D1), // MediumPink
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Course Subject:",
                            style = MaterialTheme.typography.labelMedium,
                            color = androidx.compose.ui.graphics.Color(0xFF5A4040),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = classEntity.subjectName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = androidx.compose.ui.graphics.Color(0xFF5A4040)
                    )
                }
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete class",
                    tint = androidx.compose.ui.graphics.Color(0xFFFF5252)
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Warning icon",
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    "Delete Class?",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete \"${classEntity.className}\"? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = MaterialTheme.shapes.large
        )
    }
}

@Composable
fun AddClassDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var className by remember { mutableStateOf("") }
    var subjectName by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Class,
                contentDescription = "Add class icon"
            )
        },
        title = {
            Text(
                "Add New Class",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = className,
                    onValueChange = {
                        className = it
                        error = ""
                    },
                    label = { Text("Class Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error.isNotEmpty() && className.isBlank(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
                OutlinedTextField(
                    value = subjectName,
                    onValueChange = {
                        subjectName = it
                        error = ""
                    },
                    label = { Text("Subject Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error.isNotEmpty() && subjectName.isBlank(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
                if (error.isNotEmpty()) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        className.isBlank() -> error = "Class name is required"
                        subjectName.isBlank() -> error = "Subject name is required"
                        else -> onConfirm(className.trim(), subjectName.trim())
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = MaterialTheme.shapes.large
    )
}