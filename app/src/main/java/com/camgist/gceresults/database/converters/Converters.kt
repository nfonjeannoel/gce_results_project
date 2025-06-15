package com.camgist.gceresults.database.converters

import com.camgist.gceresults.database.StudentResult
import com.camgist.gceresults.network.ResultData

fun toDatabaseModel(studentResults : List<ResultData>): List<StudentResult> {
    return studentResults.map {
        StudentResult(
            record_id = it.record_id,
            center_name = it.center_name,
            center_number = it.center_number,
            level = it.level,
            year = it.year,
            papers_passed = it.papers_passed,
            student_name = it.student_name,
            student_grades = it.student_grades,
            entry_date = it.entry_date
        )
    }
}
fun toOnlineModel(studentResults: List<StudentResult>): List<ResultData> {
    return studentResults.map {
        ResultData(
            record_id = it.record_id,
            center_name = it.center_name,
            center_number = it.center_number,
            level = it.level,
            year = it.year,
            papers_passed = it.papers_passed,
            student_name = it.student_name,
            student_grades = it.student_grades,
            entry_date = it.entry_date
        )
    }
}










