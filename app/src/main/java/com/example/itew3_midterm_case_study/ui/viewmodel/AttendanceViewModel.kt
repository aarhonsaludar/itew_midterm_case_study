package com.example.itew3_midterm_case_study.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itew3_midterm_case_study.data.entities.AttendanceEntity
import com.example.itew3_midterm_case_study.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// viewmodel managing attendance-related UI state and business logic
class AttendanceViewModel(private val repository: AttendanceRepository) : ViewModel() {

    // record or update attendance for a student on a specific date
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

    // retrieve attendance record for a specific student and date
    suspend fun getAttendance(studentId: Int, date: String): AttendanceEntity? {
        return repository.getAttendance(studentId, date)
    }

    // get all attendance records for a specific date and class
    fun getAttendanceByDate(date: String, classId: Int): Flow<List<AttendanceEntity>> {
        return repository.getAttendanceByDate(date, classId)
    }

    // get all attendance records for a specific student
    fun getAttendanceByStudent(studentId: Int): Flow<List<AttendanceEntity>> {
        return repository.getAttendanceByStudent(studentId)
    }

    // calculate attendance statistics for a student (all time)
    suspend fun calculateAttendanceStats(studentId: Int): AttendanceStats {
        val present = repository.getAttendanceCountByStatus(studentId, "Present")
        val absent = repository.getAttendanceCountByStatus(studentId, "Absent")
        val late = repository.getAttendanceCountByStatus(studentId, "Late")
        val total = present + absent + late
        // calculate percentage including late as present
        val percentage = if (total > 0) ((present + late).toFloat() / total * 100) else 0f

        return AttendanceStats(present, absent, late, total, percentage)
    }

    // calculate attendance statistics for a student within a date range
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

    // retrieve attendance records within a date range
    suspend fun getAttendanceByDateRange(
        studentId: Int,
        startDate: String,
        endDate: String
    ): List<AttendanceEntity> {
        return repository.getAttendanceByDateRange(studentId, startDate, endDate)
    }
}

// data class holding calculated attendance statistics
data class AttendanceStats(
    val present: Int, // number of present days
    val absent: Int, // number of absent days
    val late: Int, // number of late days
    val total: Int, // total attendance records
    val percentage: Float // attendance percentage (present + late / total * 100)
)
