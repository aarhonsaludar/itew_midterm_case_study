package com.example.itew3_midterm_case_study.data.dao

import androidx.room.*
import com.example.itew3_midterm_case_study.data.entities.AttendanceEntity
import kotlinx.coroutines.flow.Flow

// data access object for attendance-related database operations
@Dao
interface AttendanceDao {
    // insert or update attendance record in the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: AttendanceEntity)

    // retrieve attendance record for a specific student on a specific date
    @Query("SELECT * FROM attendance WHERE student_id = :studentId AND date = :date")
    suspend fun getAttendance(studentId: Int, date: String): AttendanceEntity?

    // get all attendance records for a specific date and class
    @Query("SELECT * FROM attendance WHERE date = :date AND student_id IN (SELECT id FROM students WHERE class_id = :classId)")
    fun getAttendanceByDate(date: String, classId: Int): Flow<List<AttendanceEntity>>

    // get all attendance records for a specific student ordered by date
    @Query("SELECT * FROM attendance WHERE student_id = :studentId ORDER BY date DESC")
    fun getAttendanceByStudent(studentId: Int): Flow<List<AttendanceEntity>>

    // retrieve attendance records within a date range for a student
    @Query("SELECT * FROM attendance WHERE student_id = :studentId AND date BETWEEN :startDate AND :endDate")
    suspend fun getAttendanceByDateRange(studentId: Int, startDate: String, endDate: String): List<AttendanceEntity>

    // count attendance records by status (present, absent, late, excused)
    @Query("SELECT COUNT(*) FROM attendance WHERE student_id = :studentId AND status = :status")
    suspend fun getAttendanceCountByStatus(studentId: Int, status: String): Int
}