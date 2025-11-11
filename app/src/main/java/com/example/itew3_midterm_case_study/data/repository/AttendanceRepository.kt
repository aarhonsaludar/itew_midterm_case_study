package com.example.itew3_midterm_case_study.data.repository

import com.example.itew3_midterm_case_study.data.dao.AttendanceDao
import com.example.itew3_midterm_case_study.data.entities.AttendanceEntity
import kotlinx.coroutines.flow.Flow

// repository layer that abstracts data access for attendance operations
class AttendanceRepository(private val attendanceDao: AttendanceDao) {
    // insert or update an attendance record
    suspend fun insert(attendance: AttendanceEntity) {
        attendanceDao.insert(attendance)
    }

    // retrieve attendance for a specific student on a specific date
    suspend fun getAttendance(studentId: Int, date: String): AttendanceEntity? {
        return attendanceDao.getAttendance(studentId, date)
    }

    // get all attendance records for a specific date and class
    fun getAttendanceByDate(date: String, classId: Int): Flow<List<AttendanceEntity>> {
        return attendanceDao.getAttendanceByDate(date, classId)
    }

    // get all attendance records for a specific student
    fun getAttendanceByStudent(studentId: Int): Flow<List<AttendanceEntity>> {
        return attendanceDao.getAttendanceByStudent(studentId)
    }

    // retrieve attendance records within a date range
    suspend fun getAttendanceByDateRange(studentId: Int, startDate: String, endDate: String): List<AttendanceEntity> {
        return attendanceDao.getAttendanceByDateRange(studentId, startDate, endDate)
    }

    // count attendance records by status (present, absent, late, excused)
    suspend fun getAttendanceCountByStatus(studentId: Int, status: String): Int {
        return attendanceDao.getAttendanceCountByStatus(studentId, status)
    }
}