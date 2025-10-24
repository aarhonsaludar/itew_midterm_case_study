package com.example.itew3_midterm_case_study.data.dao

import androidx.room.*
import com.example.itew3_midterm_case_study.data.entities.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: AttendanceEntity)

    @Query("SELECT * FROM attendance WHERE student_id = :studentId AND date = :date")
    suspend fun getAttendance(studentId: Int, date: String): AttendanceEntity?

    @Query("SELECT * FROM attendance WHERE date = :date AND student_id IN (SELECT id FROM students WHERE class_id = :classId)")
    fun getAttendanceByDate(date: String, classId: Int): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE student_id = :studentId ORDER BY date DESC")
    fun getAttendanceByStudent(studentId: Int): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE student_id = :studentId AND date BETWEEN :startDate AND :endDate")
    suspend fun getAttendanceByDateRange(studentId: Int, startDate: String, endDate: String): List<AttendanceEntity>

    @Query("SELECT COUNT(*) FROM attendance WHERE student_id = :studentId AND status = :status")
    suspend fun getAttendanceCountByStatus(studentId: Int, status: String): Int
}