package com.example.itew3_midterm_case_study.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// database entity representing an attendance record
// uses foreign key to link attendance to student, cascade delete when student is removed
@Entity(
    tableName = "attendance",
    foreignKeys = [ForeignKey(
        entity = StudentEntity::class,
        parentColumns = ["id"],
        childColumns = ["student_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // unique identifier for the attendance record
    @ColumnInfo(name = "date") val date: String, // date of attendance in ISO format (yyyy-MM-dd)
    @ColumnInfo(name = "student_id") val studentId: Int, // reference to the student
    @ColumnInfo(name = "status") val status: String // attendance status: Present, Absent, Late, or Excused
)