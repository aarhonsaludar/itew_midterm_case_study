package com.example.itew3_midterm_case_study.data.repository

import com.example.itew3_midterm_case_study.data.dao.StudentDao
import com.example.itew3_midterm_case_study.data.entities.StudentEntity
import kotlinx.coroutines.flow.Flow

class StudentRepository(private val studentDao: StudentDao) {
    fun getStudentsByClass(classId: Int): Flow<List<StudentEntity>> {
        return studentDao.getStudentsByClass(classId)
    }

    suspend fun insert(student: StudentEntity): Long {
        return studentDao.insert(student)
    }

    suspend fun update(student: StudentEntity) {
        studentDao.update(student)
    }

    suspend fun delete(student: StudentEntity) {
        studentDao.delete(student)
    }

    suspend fun getStudentById(studentId: Int): StudentEntity? {
        return studentDao.getStudentById(studentId)
    }

    suspend fun getStudentByIdNumber(idNumber: String, classId: Int): StudentEntity? {
        return studentDao.getStudentByIdNumber(idNumber, classId)
    }
}