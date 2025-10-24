package com.example.itew3_midterm_case_study.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itew3_midterm_case_study.data.entities.ClassEntity
import com.example.itew3_midterm_case_study.data.repository.ClassRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClassViewModel(private val repository: ClassRepository) : ViewModel() {
    val allClasses: StateFlow<List<ClassEntity>> = repository.allClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addClass(className: String, subjectName: String) {
        viewModelScope.launch {
            val classEntity = ClassEntity(className = className, subjectName = subjectName)
            repository.insert(classEntity)
        }
    }

    fun updateClass(classEntity: ClassEntity) {
        viewModelScope.launch {
            repository.update(classEntity)
        }
    }

    fun deleteClass(classEntity: ClassEntity) {
        viewModelScope.launch {
            repository.delete(classEntity)
        }
    }
}