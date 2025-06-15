package com.camgist.gceresults.resultlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.camgist.gceresults.network.ResultData
import com.camgist.gceresults.resultlist.details.algorithms.getRecords

class SharedViewModel : ViewModel() {

    private var _resultsToDisplay = MutableLiveData<List<ResultData>>()
    val resultsToDisplay: LiveData<List<ResultData>>
        get() = _resultsToDisplay


    private var _results = MutableLiveData<List<ResultData>>()
    val results: LiveData<List<ResultData>>
        get() = _results


    private var _result = MutableLiveData<ResultData>()
    val result: LiveData<ResultData>
        get() = _result

    private var _searchText = MutableLiveData<String?>()
    val searchText: LiveData<String?>
        get() = _searchText

    fun setStudentResult(studentResult: ResultData) {
        _result.value = studentResult
    }


    fun setResults(results: List<ResultData>) {
//        Log.d("SharedViewModel", "setResults: ${results.size}")
        // set results to display variable only if it is empty
        if (_resultsToDisplay.value == null){
            _resultsToDisplay.value = results
//            Log.d("SharedViewModel", "set initial Results: ${_resultsToDisplay.value?.size}")
        }

        _results.value = results
    }


    fun sortPoints() {
        _resultsToDisplay.value = _results.value?.sortedByDescending {
            val studentRecord = getRecords(it.student_grades ?: "", it.level, it.year)
            var total = 0
            for (record in studentRecord) {
                total += record.point
            }

            total
        }?.toMutableList()

//        Log.d("SharedViewModel", "sortPoints: ${_resultsToDisplay.value?.size}")
    }


    fun sortPapers() {
        _resultsToDisplay.value = _results.value?.sortedByDescending {
            it.papers_passed?.toIntOrNull() ?: 0
        }?.toMutableList()

//        Log.d("SharedViewModel", "sortPapers: ${_resultsToDisplay.value?.size}")
    }


    fun search(text: String?) {

        val matchedPeople = arrayListOf<ResultData>()
        text?.let {
            _results.value?.forEach { result ->
                if (result.student_name.contains(text, true) ||
                    result.center_number.contains(text, true) ||
                    result.center_name.contains(text, true) ||
                    (result.papers_passed?.toString() ?: "").contains(text, true)
                ) {
                    matchedPeople.add(result)
                }
            }

            _resultsToDisplay.value = matchedPeople.toMutableList()

        }

        _searchText.value = text

//        Log.d("SharedViewModel", "search: ${_resultsToDisplay.value?.size}")
    }

    fun clearFilter() {
        _resultsToDisplay.value = _results.value

//        Log.d("SharedViewModel", "clearFilter: ${_resultsToDisplay.value?.size}")
    }


}