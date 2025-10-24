package com.example.itew3_midterm_case_study.data.entities


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "students",
    foreignKeys = [ForeignKey(
        entity = ClassEntity::class,
        parentColumns = ["id"],
        childColumns = ["class_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "student_name") val studentName: String,
    @ColumnInfo(name = "student_id_number") val studentIdNumber: String,
    @ColumnInfo(name = "class_id") val classId: Int
)