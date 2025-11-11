package com.example.itew3_midterm_case_study.data.repository

import com.example.itew3_midterm_case_study.data.dao.ClassDao
import com.example.itew3_midterm_case_study.data.entities.ClassEntity
import kotlinx.coroutines.flow.Flow

// repository layer that abstracts data access for class operations
class ClassRepository(private val classDao: ClassDao) {
    // flow of all classes, automatically updates when database changes
    val allClasses: Flow<List<ClassEntity>> = classDao.getAllClasses()

    // insert a new class and return its ID
    suspend fun insert(classEntity: ClassEntity): Long {
        return classDao.insert(classEntity)
    }

    // update existing class information
    suspend fun update(classEntity: ClassEntity) {
        classDao.update(classEntity)
    }

    // delete a class from the database
    suspend fun delete(classEntity: ClassEntity) {
        classDao.delete(classEntity)
    }

    // retrieve a specific class by its ID
    suspend fun getClassById(classId: Int): ClassEntity? {
        return classDao.getClassById(classId)
    }
}