package com.example.itew3_midterm_case_study.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "date") val date: String, // Format: "yyyy-MM-dd"
    @ColumnInfo(name = "student_id") val studentId: Int,
    @ColumnInfo(name = "status") val status: String // "Present", "Absent", "Late"
)