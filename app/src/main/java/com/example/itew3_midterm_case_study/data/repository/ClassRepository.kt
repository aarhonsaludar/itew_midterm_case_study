package com.example.itew3_midterm_case_study.data.repository

import com.example.itew3_midterm_case_study.data.dao.ClassDao
import com.example.itew3_midterm_case_study.data.entities.ClassEntity
import kotlinx.coroutines.flow.Flow

class ClassRepository(private val classDao: ClassDao) {
    val allClasses: Flow<List<ClassEntity>> = classDao.getAllClasses()

    suspend fun insert(classEntity: ClassEntity): Long {
        return classDao.insert(classEntity)
    }

    suspend fun update(classEntity: ClassEntity) {
        classDao.update(classEntity)
    }

    suspend fun delete(classEntity: ClassEntity) {
        classDao.delete(classEntity)
    }

    suspend fun getClassById(classId: Int): ClassEntity? {
        return classDao.getClassById(classId)
    }
}