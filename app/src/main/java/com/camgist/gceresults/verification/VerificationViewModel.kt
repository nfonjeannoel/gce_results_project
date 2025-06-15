package com.camgist.gceresults.verification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camgist.gceresults.network.ResultsApi
import kotlinx.coroutines.launch

class VerificationViewModel() : ViewModel() {
    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<Int?>()

    // The external immutable LiveData for the response String
    val response: LiveData<Int?>
        get() = _response

    // The internal MutableLiveData String that stores the most recent response
    private val _error = MutableLiveData<String?>()

    // The external immutable LiveData for the response String
    val error: LiveData<String?>
        get() = _error

    // Loading state
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    fun getResultFromApi(filters: HashMap<String, String>) {
        viewModelScope.launch {
            _loading.value = true
            // Clear previous states
            _response.value = null
            _error.value = null
            
            try {
                val listResult = ResultsApi.resultsService.searchResults(filters)
                Log.d("VerificationViewModel", "Verification result: ${listResult.status}")
                _response.value = listResult.status
                
                // Log the verification result for debugging
                when (listResult.status) {
                    1 -> Log.d("VerificationViewModel", "Results are available")
                    -1 -> Log.d("VerificationViewModel", "No results found: ${listResult.msg}")
                    else -> Log.w("VerificationViewModel", "Unexpected status: ${listResult.status}")
                }
                
            } catch (e: Exception) {
                Log.e("VerificationViewModel", "Verification failed", e)
                _error.value = when {
                    e.message?.contains("timeout", true) == true -> 
                        "Request timed out. Please check your internet connection and try again."
                    e.message?.contains("network", true) == true -> 
                        "Network error. Please check your internet connection."
                    e.message?.contains("host", true) == true -> 
                        "Unable to connect to server. Please try again later."
                    e.message?.contains("validation", true) == true ->
                        e.message ?: "Please check your input and try again."
                    else -> "An error occurred: ${e.message ?: "Unknown error"}"
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun doneShowingError() {
        _error.value = null
    }

    fun doneShowingResponse() {
        _response.value = null
    }
}