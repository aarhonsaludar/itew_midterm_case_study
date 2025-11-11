package com.example.itew3_midterm_case_study.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// database entity representing a class/course in the attendance system
@Entity(tableName = "classes")
data class ClassEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // unique identifier for the class
    @ColumnInfo(name = "class_name") val className: String, // name of the section/class
    @ColumnInfo(name = "subject_name") val subjectName: String // subject/course name
)