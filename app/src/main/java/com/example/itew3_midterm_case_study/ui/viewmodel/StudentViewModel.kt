package com.example.itew3_midterm_case_study.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itew3_midterm_case_study.data.entities.StudentEntity
import com.example.itew3_midterm_case_study.data.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {

    fun getStudentsByClass(classId: Int): Flow<List<StudentEntity>> {
        return repository.getStudentsByClass(classId)
    }

    fun addStudent(studentName: String, studentIdNumber: String, classId: Int) {
        viewModelScope.launch {
            val student = StudentEntity(
                studentName = studentName,
                studentIdNumber = studentIdNumber,
                classId = classId
            )
            repository.insert(student)
        }
    }

    fun updateStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.update(student)
        }
    }

    fun deleteStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.delete(student)
        }
    }

    suspend fun checkStudentIdExists(idNumber: String, classId: Int): Boolean {
        return repository.getStudentByIdNumber(idNumber, classId) != null
    }
}