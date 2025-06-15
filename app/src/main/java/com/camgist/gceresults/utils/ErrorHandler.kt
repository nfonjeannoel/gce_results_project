package com.camgist.gceresults.utils

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.camgist.gceresults.network.ErrorType

object ErrorHandler {
    
    /**
     * Show appropriate error message based on error type
     */
    fun showError(context: Context, errorMessage: String?, errorType: ErrorType? = null) {
        val message = when (errorType) {
            ErrorType.NETWORK_ERROR -> "No internet connection. Please check your network settings."
            ErrorType.TIMEOUT_ERROR -> "Request timed out. Please try again."
            ErrorType.SERVER_ERROR -> "Server error. Please try again later."
            ErrorType.AUTHENTICATION_ERROR -> "Authentication failed. Please contact support."
            ErrorType.VALIDATION_ERROR -> errorMessage ?: "Please check your input."
            ErrorType.UNKNOWN_ERROR -> "An unexpected error occurred. Please try again."
            null -> errorMessage ?: "An error occurred. Please try again."
        }
        
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    
    /**
     * Check if the error is a network-related error that might be temporary
     */
    fun isRetryableError(errorMessage: String?): Boolean {
        return errorMessage?.let { msg ->
            msg.contains("timeout", true) ||
            msg.contains("network", true) ||
            msg.contains("connection", true) ||
            msg.contains("host", true)
        } ?: false
    }
    
    /**
     * Get user-friendly error message for common errors
     */
    fun getUserFriendlyMessage(errorMessage: String?): String {
        return when {
            errorMessage == null -> "An unknown error occurred"
            errorMessage.contains("timeout", true) -> 
                "The request is taking too long. Please check your internet connection and try again."
            errorMessage.contains("network", true) || errorMessage.contains("host", true) -> 
                "Unable to connect to the server. Please check your internet connection."
            errorMessage.contains("404", true) -> 
                "Service temporarily unavailable. Please try again later."
            errorMessage.contains("500", true) || errorMessage.contains("502", true) || errorMessage.contains("503", true) -> 
                "Server is experiencing issues. Please try again later."
            errorMessage.contains("auth", true) -> 
                "Authentication failed. Please contact support."
            errorMessage.length > 100 -> 
                "An error occurred while processing your request. Please try again."
            else -> errorMessage
        }
    }
    
    /**
     * Validate common input fields
     */
    fun validateSearchInput(level: String?, year: String?, searchTerm: String?): String? {
        return when {
            level.isNullOrBlank() -> "Please select a level"
            year.isNullOrBlank() -> "Please enter a year"
            searchTerm.isNullOrBlank() -> "Please enter a search term"
            year.toIntOrNull() == null -> "Please enter a valid year"
            year.toInt() < 2000 || year.toInt() > 2030 -> "Please enter a valid year (2000-2030)"
            searchTerm.length < 2 -> "Search term must be at least 2 characters long"
            else -> null
        }
    }
} 