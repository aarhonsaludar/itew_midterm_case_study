package com.example.itew3_midterm_case_study.data.dao

import androidx.room.*
import com.example.itew3_midterm_case_study.data.entities.ClassEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassDao {
    @Insert
    suspend fun insert(classEntity: ClassEntity): Long

    @Update
    suspend fun update(classEntity: ClassEntity)

    @Delete
    suspend fun delete(classEntity: ClassEntity)

    @Query("SELECT * FROM classes ORDER BY class_name ASC")
    fun getAllClasses(): Flow<List<ClassEntity>>

    @Query("SELECT * FROM classes WHERE id = :classId")
    suspend fun getClassById(classId: Int): ClassEntity?
}