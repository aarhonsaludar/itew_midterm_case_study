package com.example.itew3_midterm_case_study.data.dao

import androidx.room.*
import com.example.itew3_midterm_case_study.data.entities.StudentEntity
import kotlinx.coroutines.flow.Flow

// data access object for student-related database operations
@Dao
interface StudentDao {
    // insert a new student and return the generated ID
    @Insert
    suspend fun insert(student: StudentEntity): Long

    // update an existing student record
    @Update
    suspend fun update(student: StudentEntity)

    // delete a student from the database
    @Delete
    suspend fun delete(student: StudentEntity)

    // retrieve all students in a specific class sorted alphabetically
    @Query("SELECT * FROM students WHERE class_id = :classId ORDER BY student_name ASC")
    fun getStudentsByClass(classId: Int): Flow<List<StudentEntity>>

    // get a specific student by their database ID
    @Query("SELECT * FROM students WHERE id = :studentId")
    suspend fun getStudentById(studentId: Int): StudentEntity?

    // find a student by their ID number within a specific class
    @Query("SELECT * FROM students WHERE student_id_number = :idNumber AND class_id = :classId")
    suspend fun getStudentByIdNumber(idNumber: String, classId: Int): StudentEntity?
}