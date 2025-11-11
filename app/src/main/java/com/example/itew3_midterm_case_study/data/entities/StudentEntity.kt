package com.example.itew3_midterm_case_study.data.entities


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// database entity representing a student in the attendance system
// uses foreign key to link student to their class, cascade delete when class is removed
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
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // unique database identifier
    @ColumnInfo(name = "student_name") val studentName: String, // full name of the student
    @ColumnInfo(name = "student_id_number") val studentIdNumber: String, // student's ID number
    @ColumnInfo(name = "class_id") val classId: Int // reference to the class this student belongs to
)