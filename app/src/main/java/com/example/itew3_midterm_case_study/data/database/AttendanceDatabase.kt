package com.example.itew3_midterm_case_study.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.itew3_midterm_case_study.data.dao.AttendanceDao
import com.example.itew3_midterm_case_study.data.dao.ClassDao
import com.example.itew3_midterm_case_study.data.dao.StudentDao
import com.example.itew3_midterm_case_study.data.entities.AttendanceEntity
import com.example.itew3_midterm_case_study.data.entities.ClassEntity
import com.example.itew3_midterm_case_study.data.entities.StudentEntity

@Database(
    entities = [ClassEntity::class, StudentEntity::class, AttendanceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AttendanceDatabase : RoomDatabase() {
    abstract fun classDao(): ClassDao
    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: AttendanceDatabase? = null

        fun getDatabase(context: Context): AttendanceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AttendanceDatabase::class.java,
                    "attendance_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}