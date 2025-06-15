package com.camgist.gceresults.resultlist.details.algorithms

import android.util.Log


fun getRecords(gradesString: String, level: String, year: Int): ArrayList<Records> {
    var gradeString = gradesString.replace(" ", "")
    val listGrade = splitGrades(gradeString)
    return formatDetails(listGrade, level, year)
}


private fun formatDetails(listGrade: List<String>, level: String, year: Int): ArrayList<Records> {
    val records = arrayListOf<Records>()
//    Log.d("Algorithm.kt", listGrade.toString())
    if (year >= 2023) {
        for (details in listGrade) {
            var subjectName = "N/A"
            var grade = "N/A"
            var point = 0
            records.add(Records(subjectName, grade.toString(), point))
        }

        return records

    } else {
        for (details in listGrade) {

            var shortName = when (details.length) {
                6 -> details.substring(0, 4)
                4 -> details.substring(0, 2)
                else -> details.substring(0, 3)
            }
            var subjectName = getSubjectName(shortName)
            var grade = details.last()
            var point = calculatePoint(level, grade)
            records.add(Records(subjectName, grade.toString(), point))
        }
        return records
    }
}

fun getSubjectName(shortName: String): String {
    return when (shortName) {
//        general
        "ENG" -> {
            "English"
        }

        "REL" -> {
            "Religion"
        }

        "ICT" -> {
            "Information and Communication Technology"
        }

        "FRE" -> {
            "French"
        }

        "  " -> {
            "  "
        }

        "  " -> {
            "  "
        }
//        science
        "BIO" -> {
            "Biology"
        }

        "CHE" -> {
            "Chemistry"
        }

        "PHY" -> {
            "Physics"
        }

        "PMN" -> {
            "Pure Mathematics (Mechanics)"
        }

        "CSC" -> {
            "Computer Science"
        }
//        "PMS" -> {"Pure Mathematics (Statistics)"}
        "FMA" -> {
            "Further Mathematics"
        }

        "  " -> {
            "  "
        }

        "  " -> {
            "  "
        }

        "  " -> {
            "  "
        }
//        arts
        "ECO" -> {
            "Economics"
        }

        "GEO" -> {
            "Geography"
        }

        "HIS" -> {
            "History"
        }

        "LIT" -> {
            "Literature"
        }

        "  " -> {
            "  "
        }

        "  " -> {
            "  "
        }

        "  " -> {
            "  "
        }

        "  " -> {
            "  "
        }
//        commercial
        "BMA" -> {
            "Business Mathematics"
        }

        "BMG" -> {
            "Business Management"
        }

        "CAC" -> {
            "Cost Accounting"
        }

        "CFN" -> {
            "Commerce and Finance"
        }

        "CMA" -> {
            "Cost and Management Accounting"
        }

        "FAC" -> {
            "Financial Accounting"
        }

        else -> shortName
    }
}

fun calculatePoint(level: String, grade: Char): Int {
    if (level == "ALG" || level == "ALT") {
        return when (grade) {
            'A' -> {
                5
            }

            'B' -> {
                4
            }

            'C' -> {
                3
            }

            'D' -> {
                2
            }

            'E' -> {
                1
            }

            else -> {
                0
            }
        }
    } else {
        return when (grade) {
            'A' -> {
                3
            }

            'B' -> {
                2
            }

            'C' -> {
                1
            }

            else -> {
                0
            }
        }
    }
}

fun splitGrades(grade: String): List<String> {
    return grade.split(",")
}

data class Records(
    val subjectName: String,
    val grade: String,
    val point: Int
)