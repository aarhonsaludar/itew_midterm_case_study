package com.example.itew3_midterm_case_study.data.dao

import androidx.room.*
import com.example.itew3_midterm_case_study.data.entities.ClassEntity
import kotlinx.coroutines.flow.Flow

// data access object for class-related database operations
@Dao
interface ClassDao {
    // insert a new class and return its generated ID
    @Insert
    suspend fun insert(classEntity: ClassEntity): Long

    // update an existing class record
    @Update
    suspend fun update(classEntity: ClassEntity)

    // delete a class from the database
    @Delete
    suspend fun delete(classEntity: ClassEntity)

    // retrieve all classes sorted alphabetically by name
    @Query("SELECT * FROM classes ORDER BY class_name ASC")
    fun getAllClasses(): Flow<List<ClassEntity>>

    // get a specific class by its ID
    @Query("SELECT * FROM classes WHERE id = :classId")
    suspend fun getClassById(classId: Int): ClassEntity?
}