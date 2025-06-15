package com.camgist.gceresults.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "student_results_db")
data class StudentResult(
    @PrimaryKey
    val record_id : Int,
    @ColumnInfo(name = "center_name")
    val center_name: String,
    @ColumnInfo(name = "center_number")
    val center_number: String,
    @ColumnInfo(name = "level")
    val level: String,
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "papers_passed")
    val papers_passed: String?,
    @ColumnInfo(name = "student_name")
    val student_name: String,
    @ColumnInfo(name = "student_grades")
    val student_grades: String?,
    @ColumnInfo(name = "entry_date")
    val entry_date : String
)
