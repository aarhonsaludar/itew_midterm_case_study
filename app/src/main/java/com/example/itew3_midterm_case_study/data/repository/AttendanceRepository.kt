package com.example.itew3_midterm_case_study.data.repository

import com.example.itew3_midterm_case_study.data.dao.AttendanceDao
import com.example.itew3_midterm_case_study.data.entities.AttendanceEntity
import kotlinx.coroutines.flow.Flow

class AttendanceRepository(private val attendanceDao: AttendanceDao) {
    suspend fun insert(attendance: AttendanceEntity) {
        attendanceDao.insert(attendance)
    }

    suspend fun getAttendance(studentId: Int, date: String): AttendanceEntity? {
        return attendanceDao.getAttendance(studentId, date)
    }

    fun getAttendanceByDate(date: String, classId: Int): Flow<List<AttendanceEntity>> {
        return attendanceDao.getAttendanceByDate(date, classId)
    }

    fun getAttendanceByStudent(studentId: Int): Flow<List<AttendanceEntity>> {
        return attendanceDao.getAttendanceByStudent(studentId)
    }

    suspend fun getAttendanceByDateRange(studentId: Int, startDate: String, endDate: String): List<AttendanceEntity> {
        return attendanceDao.getAttendanceByDateRange(studentId, startDate, endDate)
    }

    suspend fun getAttendanceCountByStatus(studentId: Int, status: String): Int {
        return attendanceDao.getAttendanceCountByStatus(studentId, status)
    }
}