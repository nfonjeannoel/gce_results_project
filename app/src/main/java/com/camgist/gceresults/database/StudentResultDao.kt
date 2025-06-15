package com.camgist.gceresults.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StudentResultDao {

    @Insert
    suspend fun insert(records : StudentResult)

    @Query("SELECT * FROM student_results_db WHERE year = :yearInt AND level = :level and center_number = :center")
    suspend fun getCenterResults(yearInt: Int, level: String, center: String?) : List<StudentResult>?

    @Query("SELECT * FROM student_results_db WHERE year = :yearInt AND level = :level")
    suspend fun getPapersCenterResults(yearInt: Int, level: String) : List<StudentResult>?
}