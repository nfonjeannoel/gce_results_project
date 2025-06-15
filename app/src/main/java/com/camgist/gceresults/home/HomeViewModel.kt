package com.camgist.gceresults.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camgist.gceresults.database.StudentResult
import com.camgist.gceresults.database.StudentResultDao
import com.camgist.gceresults.database.converters.toDatabaseModel
import com.camgist.gceresults.database.converters.toOnlineModel
import com.camgist.gceresults.network.ResultData
import com.camgist.gceresults.network.ResultsApi
import com.camgist.gceresults.network.ErrorType
import com.camgist.gceresults.utils.NetworkUtils
import kotlinx.coroutines.launch

class HomeViewModel(val database: StudentResultDao) : ViewModel() {
    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<String?>()

    // The external immutable LiveData for the response String
    val response: LiveData<String?>
        get() = _response


    // The internal MutableLiveData String that stores the most recent response
    private val _noMatch = MutableLiveData<String?>()

    // The external immutable LiveData for the response String
    val noMatch: LiveData<String?>
        get() = _noMatch


    // The internal MutableLiveData String that stores the most recent response
    private val _results = MutableLiveData<List<ResultData>?>()

    // The external immutable LiveData for the response String
    val results: LiveData<List<ResultData>?>
        get() = _results

    // The internal MutableLiveData String that stores the most recent response
    private val _databaseResults = MutableLiveData<List<StudentResult>?>()

    // The external immutable LiveData for the response String
    val databaseResults: LiveData<List<StudentResult>?>
        get() = _databaseResults

    // The internal MutableLiveData String that stores the most recent response
    private val _showToast = MutableLiveData<Boolean>()

    // The external immutable LiveData for the response String
    val showToast: LiveData<Boolean>
        get() = _showToast

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean>
        get() = _showLoading


    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
//    init {
//        getMarsRealEstateProperties()
//    }

    /**
     * Enhanced search function with comprehensive error handling and loading states
     */
    fun getResultFromApi(filters: HashMap<String, String>, shouldSave: Boolean = false) {
        viewModelScope.launch {
            _showLoading.value = true
            _showToast.value = true
            // Clear previous states
            _response.value = null
            _noMatch.value = null
            _results.value = null
            
            try {
                val listResult = ResultsApi.resultsService.searchResults(filters)
                Log.d("HomeViewModel", "API Response: $listResult")
                
                when (listResult.status) {
                    1 -> {
                        _results.value = listResult.data
                        if (shouldSave && listResult.data != null) {
                            insertToDatabase(toDatabaseModel(listResult.data))
                        }
                    }
                    -1 -> {
                        // Check if the error message indicates a network issue
                        val errorMsg = listResult.msg ?: ""
                        if (isNetworkError(errorMsg)) {
                            _response.value = errorMsg
                        } else {
                            _noMatch.value = errorMsg
                        }
                    }
                    else -> {
                        _response.value = "Unexpected response status: ${listResult.status}"
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "API call failed", e)
                _response.value = when {
                    e.message?.contains("timeout", true) == true -> 
                        "Request timed out. Please check your internet connection and try again."
                    e.message?.contains("network", true) == true -> 
                        "Network error. Please check your internet connection."
                    e.message?.contains("host", true) == true -> 
                        "Unable to connect to server. Please try again later."
                    else -> "An error occurred: ${e.message ?: "Unknown error"}"
                }
            } finally {
                _showLoading.value = false
            }
        }
    }

    /**
     * Helper function to determine if an error message indicates a network issue
     */
    private fun isNetworkError(errorMessage: String): Boolean {
        return errorMessage.contains("internet", true) ||
               errorMessage.contains("network", true) ||
               errorMessage.contains("connection", true) ||
               errorMessage.contains("timeout", true) ||
               errorMessage.contains("host", true) ||
               errorMessage.contains("connectivity", true)
    }

    fun showingToastComplete() {
        _showToast.value = false
    }

    fun fetchFromDatabase(filters: HashMap<String, String>) {
        viewModelScope.launch {
            _showLoading.value = true
            try {
                val year = filters["year"]?.toIntOrNull()
                val level = filters["level"]
                val center = filters["center_number"]
                
                if (year == null || level == null) {
                    _response.value = "Invalid search parameters"
                    return@launch
                }
                
                val tempDatabaseResults = getResultsFromDatabase(year, level, center)
                if (tempDatabaseResults.isNullOrEmpty()) {
                    // No local data, check network before trying API
                    _response.value = "No internet connection. Please connect to the internet to search for new results."
                } else {
                    _results.value = toOnlineModel(tempDatabaseResults)
                    Log.d("HomeViewModel", "Loaded ${tempDatabaseResults.size} results from database")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Database fetch failed", e)
                // Check network before fallback to API
                _response.value = "No internet connection. Please connect to the internet to search for results."
            } finally {
                _showLoading.value = false
            }
        }
    }

    fun fetchCenterFromDatabase(filters: HashMap<String, String>) {
        viewModelScope.launch {
            _showLoading.value = true
            try {
                val year = filters["year"]?.toIntOrNull()
                val level = filters["level"]
                
                if (year == null || level == null) {
                    _response.value = "Invalid search parameters"
                    return@launch
                }
                
                val tempDatabaseResults = getCenterResultsFromDatabase(year, level)
                if (tempDatabaseResults.isNullOrEmpty()) {
                    // No local data, check network before trying API
                    _response.value = "No internet connection. Please connect to the internet to search for new results."
                } else {
                    _results.value = toOnlineModel(tempDatabaseResults)
                    Log.d("HomeViewModel", "Loaded ${tempDatabaseResults.size} center results from database")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Database center fetch failed", e)
                // Check network before fallback to API
                _response.value = "No internet connection. Please connect to the internet to search for results."
            } finally {
                _showLoading.value = false
            }
        }
    }

    /**
     * Enhanced version of fetchFromDatabase that includes network connectivity checks
     * Should be called with a context to check network status
     */
    fun fetchFromDatabaseWithNetworkCheck(filters: HashMap<String, String>, hasNetworkConnection: Boolean) {
        viewModelScope.launch {
            _showLoading.value = true
            try {
                val year = filters["year"]?.toIntOrNull()
                val level = filters["level"]
                val center = filters["center_number"]
                
                if (year == null || level == null) {
                    _response.value = "Invalid search parameters"
                    return@launch
                }
                
                val tempDatabaseResults = getResultsFromDatabase(year, level, center)
                if (tempDatabaseResults.isNullOrEmpty()) {
                    // No local data, check if we have internet connection
                    if (hasNetworkConnection) {
                        // Try API
                        getResultFromApi(filters, true)
                    } else {
                        // No internet and no cached data
                        _response.value = "No internet connection. Please connect to the internet to search for new results."
                    }
                } else {
                    _results.value = toOnlineModel(tempDatabaseResults)
                    Log.d("HomeViewModel", "Loaded ${tempDatabaseResults.size} results from database")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Database fetch failed", e)
                // Fallback to API only if network is available
                if (hasNetworkConnection) {
                    getResultFromApi(filters, true)
                } else {
                    _response.value = "No internet connection. Please connect to the internet to search for results."
                }
            } finally {
                if (_results.value != null) {
                    _showLoading.value = false
                }
            }
        }
    }

    /**
     * Enhanced version of fetchCenterFromDatabase that includes network connectivity checks
     */
    fun fetchCenterFromDatabaseWithNetworkCheck(filters: HashMap<String, String>, hasNetworkConnection: Boolean) {
        viewModelScope.launch {
            _showLoading.value = true
            try {
                val year = filters["year"]?.toIntOrNull()
                val level = filters["level"]
                
                if (year == null || level == null) {
                    _response.value = "Invalid search parameters"
                    return@launch
                }
                
                val tempDatabaseResults = getCenterResultsFromDatabase(year, level)
                if (tempDatabaseResults.isNullOrEmpty()) {
                    // No local data, check if we have internet connection
                    if (hasNetworkConnection) {
                        // Try API
                        getResultFromApi(filters, true)
                    } else {
                        // No internet and no cached data
                        _response.value = "No internet connection. Please connect to the internet to search for new results."
                    }
                } else {
                    _results.value = toOnlineModel(tempDatabaseResults)
                    Log.d("HomeViewModel", "Loaded ${tempDatabaseResults.size} center results from database")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Database center fetch failed", e)
                // Fallback to API only if network is available
                if (hasNetworkConnection) {
                    getResultFromApi(filters, true)
                } else {
                    _response.value = "No internet connection. Please connect to the internet to search for results."
                }
            } finally {
                if (_results.value != null) {
                    _showLoading.value = false
                }
            }
        }
    }

    private suspend fun insertToDatabase(tempDatabaseResults: List<StudentResult>) {
        tempDatabaseResults.forEach {
            database?.insert(it)
        }
    }

    private suspend fun getCenterResultsFromDatabase(
        year: Int,
        level: String
    ): List<StudentResult>? {
        return database?.getPapersCenterResults(year, level)
    }


    private suspend fun getResultsFromDatabase(
        year: Int,
        level: String,
        center: String?
    ): List<StudentResult>? {
        return database?.getCenterResults(year, level, center)
    }

    fun doneNavigatingListResult() {
        _results.value = null
    }

    fun doneShowingError() {
        _response.value = null
    }

    fun doneShowingNoMatch() {
        _noMatch.value = null
    }
}