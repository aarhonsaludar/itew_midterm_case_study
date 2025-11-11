package com.example.itew3_midterm_case_study.data.repository

import com.example.itew3_midterm_case_study.data.dao.StudentDao
import com.example.itew3_midterm_case_study.data.entities.StudentEntity
import kotlinx.coroutines.flow.Flow

// repository layer that abstracts data access for student operations
class StudentRepository(private val studentDao: StudentDao) {
    // get all students in a specific class as a flow
    fun getStudentsByClass(classId: Int): Flow<List<StudentEntity>> {
        return studentDao.getStudentsByClass(classId)
    }

    // insert a new student and return the generated ID
    suspend fun insert(student: StudentEntity): Long {
        return studentDao.insert(student)
    }

    // update existing student information
    suspend fun update(student: StudentEntity) {
        studentDao.update(student)
    }

    // delete a student from the database
    suspend fun delete(student: StudentEntity) {
        studentDao.delete(student)
    }

    // retrieve a specific student by database ID
    suspend fun getStudentById(studentId: Int): StudentEntity? {
        return studentDao.getStudentById(studentId)
    }

    // find a student by their ID number within a class
    suspend fun getStudentByIdNumber(idNumber: String, classId: Int): StudentEntity? {
        return studentDao.getStudentByIdNumber(idNumber, classId)
    }
}