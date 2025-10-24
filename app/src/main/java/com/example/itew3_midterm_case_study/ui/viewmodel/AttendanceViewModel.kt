package com.example.itew3_midterm_case_study.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itew3_midterm_case_study.data.entities.AttendanceEntity
import com.example.itew3_midterm_case_study.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AttendanceViewModel(private val repository: AttendanceRepository) : ViewModel() {

    fun markAttendance(studentId: Int, date: String, status: String) {
        viewModelScope.launch {
            val attendance = AttendanceEntity(
                studentId = studentId,
                date = date,
                status = status
            )
            repository.insert(attendance)
        }
    }

    suspend fun getAttendance(studentId: Int, date: String): AttendanceEntity? {
        return repository.getAttendance(studentId, date)
    }

    fun getAttendanceByDate(date: String, classId: Int): Flow<List<AttendanceEntity>> {
        return repository.getAttendanceByDate(date, classId)
    }

    fun getAttendanceByStudent(studentId: Int): Flow<List<AttendanceEntity>> {
        return repository.getAttendanceByStudent(studentId)
    }

    suspend fun calculateAttendanceStats(studentId: Int): AttendanceStats {
        val present = repository.getAttendanceCountByStatus(studentId, "Present")
        val absent = repository.getAttendanceCountByStatus(studentId, "Absent")
        val late = repository.getAttendanceCountByStatus(studentId, "Late")
        val total = present + absent + late
        val percentage = if (total > 0) ((present + late).toFloat() / total * 100) else 0f

        return AttendanceStats(present, absent, late, total, percentage)
    }

    suspend fun calculateAttendanceStatsByDateRange(
        studentId: Int,
        startDate: String,
        endDate: String
    ): AttendanceStats {
        val attendanceList = repository.getAttendanceByDateRange(studentId, startDate, endDate)
        val present = attendanceList.count { it.status == "Present" }
        val absent = attendanceList.count { it.status == "Absent" }
        val late = attendanceList.count { it.status == "Late" }
        val total = attendanceList.size
        val percentage = if (total > 0) ((present + late).toFloat() / total * 100) else 0f

        return AttendanceStats(present, absent, late, total, percentage)
    }

    suspend fun getAttendanceByDateRange(
        studentId: Int,
        startDate: String,
        endDate: String
    ): List<AttendanceEntity> {
        return repository.getAttendanceByDateRange(studentId, startDate, endDate)
    }
}

data class AttendanceStats(
    val present: Int,
    val absent: Int,
    val late: Int,
    val total: Int,
    val percentage: Float
)