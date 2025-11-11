package com.example.itew3_midterm_case_study.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itew3_midterm_case_study.data.entities.ClassEntity
import com.example.itew3_midterm_case_study.data.repository.ClassRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// viewmodel managing class-related UI state and business logic
class ClassViewModel(private val repository: ClassRepository) : ViewModel() {
    // state flow of all classes, cached for 5 seconds after last subscriber
    val allClasses: StateFlow<List<ClassEntity>> = repository.allClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // add a new class to the database
    fun addClass(className: String, subjectName: String) {
        viewModelScope.launch {
            val classEntity = ClassEntity(className = className, subjectName = subjectName)
            repository.insert(classEntity)
        }
    }

    // update existing class information
    fun updateClass(classEntity: ClassEntity) {
        viewModelScope.launch {
            repository.update(classEntity)
        }
    }

    // delete a class and all associated students/attendance (cascade)
    fun deleteClass(classEntity: ClassEntity) {
        viewModelScope.launch {
            repository.delete(classEntity)
        }
    }
}