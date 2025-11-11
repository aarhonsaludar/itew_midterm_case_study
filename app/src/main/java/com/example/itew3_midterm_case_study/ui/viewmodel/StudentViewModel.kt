package com.example.itew3_midterm_case_study.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itew3_midterm_case_study.data.entities.StudentEntity
import com.example.itew3_midterm_case_study.data.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// viewmodel managing student-related UI state and business logic
class StudentViewModel(private val repository: StudentRepository) : ViewModel() {

    // retrieve all students in a specific class as a flow
    fun getStudentsByClass(classId: Int): Flow<List<StudentEntity>> {
        return repository.getStudentsByClass(classId)
    }

    // add a new student to a class
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

    // update existing student information
    fun updateStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.update(student)
        }
    }

    // delete a student and all associated attendance records (cascade)
    fun deleteStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.delete(student)
        }
    }

    // check if a student ID already exists in a class to prevent duplicates
    suspend fun checkStudentIdExists(idNumber: String, classId: Int): Boolean {
        return repository.getStudentByIdNumber(idNumber, classId) != null
    }
}