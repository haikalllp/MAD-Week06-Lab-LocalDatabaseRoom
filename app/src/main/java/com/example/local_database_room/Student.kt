package com.example.local_database_room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "student_name")
    val name: String,
    @ColumnInfo(name = "student_age")
    val age: Int
)
