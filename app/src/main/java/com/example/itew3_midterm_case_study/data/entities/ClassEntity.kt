package com.example.itew3_midterm_case_study.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classes")
data class ClassEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "class_name") val className: String,
    @ColumnInfo(name = "subject_name") val subjectName: String
)