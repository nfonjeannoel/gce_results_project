package com.camgist.gceresults.network

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import okhttp3.OkHttpClient
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

// Updated to use Supabase RPC functions
private const val BASE_URL = "https://xyjxbwpakxxvqhxubklt.supabase.co/"
private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inh5anhid3Bha3h4dnFoeHVia2x0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDk3ODUwOTgsImV4cCI6MjA2NTM2MTA5OH0.i46yXNGLOz36g0QMIgD-nHtQPPX4PMDCnIu6lgRNs7A"

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

// Enhanced OkHttpClient with proper timeouts
private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

interface ResultsApiService {
    @POST("rest/v1/rpc/search_by_student_name")
    suspend fun searchByStudentName(
        @Header("apikey") apiKey: String = SUPABASE_ANON_KEY,
        @Header("Authorization") authorization: String = "Bearer $SUPABASE_ANON_KEY",
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: SearchByNameRequest
    ): List<ResultData>

    @POST("rest/v1/rpc/search_by_center_number")
    suspend fun searchByCenterNumber(
        @Header("apikey") apiKey: String = SUPABASE_ANON_KEY,
        @Header("Authorization") authorization: String = "Bearer $SUPABASE_ANON_KEY",
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: SearchByCenterNumberRequest
    ): List<ResultData>

    @POST("rest/v1/rpc/search_by_center_name")
    suspend fun searchByCenterName(
        @Header("apikey") apiKey: String = SUPABASE_ANON_KEY,
        @Header("Authorization") authorization: String = "Bearer $SUPABASE_ANON_KEY",
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: SearchByCenterNameRequest
    ): List<ResultData>

    @POST("rest/v1/rpc/get_levels")
    suspend fun getLevels(
        @Header("apikey") apiKey: String = SUPABASE_ANON_KEY,
        @Header("Authorization") authorization: String = "Bearer $SUPABASE_ANON_KEY",
        @Header("Content-Type") contentType: String = "application/json"
    ): List<LevelData>

    @POST("rest/v1/rpc/get_years")
    suspend fun getYears(
        @Header("apikey") apiKey: String = SUPABASE_ANON_KEY,
        @Header("Authorization") authorization: String = "Bearer $SUPABASE_ANON_KEY",
        @Header("Content-Type") contentType: String = "application/json"
    ): List<YearData>
}

// Enhanced error handling with specific error types
enum class ErrorType {
    NETWORK_ERROR,
    TIMEOUT_ERROR,
    SERVER_ERROR,
    AUTHENTICATION_ERROR,
    VALIDATION_ERROR,
    UNKNOWN_ERROR
}

data class AppError(
    val type: ErrorType,
    val message: String,
    val details: String? = null
)

class ResultsService(private val apiService: ResultsApiService) {
    
    suspend fun searchResults(filters: HashMap<String, String>): Results {
        return try {
            // Input validation
            val validationError = validateInput(filters)
            if (validationError != null) {
                return createErrorResult(validationError.message, validationError.type)
            }

            val searchType = determineSearchType(filters)
            val searchValue = getSearchValue(filters)
            val level = filters["level"]!!
            val year = filters["year"]!!
            val papersPassedFilter = filters["papers_passed"]

            Log.d("ResultsService", "Searching: type=$searchType, value=$searchValue, level=$level, year=$year")

            val results = performSearch(searchType, searchValue, level, year)

            // Filter by papers_passed if specified (for PapersFragment - opt "3")
            val filteredResults = applyPapersFilter(results, papersPassedFilter)

            if (filteredResults.isNotEmpty()) {
                Log.d("ResultsService", "Found ${filteredResults.size} results")
                Results(
                    status = 1,
                    data = filteredResults,
                    count = filteredResults.size,
                    msg = "Success"
                )
            } else {
                Log.d("ResultsService", "No results found")
                Results(
                    status = -1,
                    data = null,
                    count = 0,
                    msg = "No records found matching your search criteria"
                )
            }
        } catch (e: Exception) {
            Log.e("ResultsService", "Search failed", e)
            val appError = mapExceptionToAppError(e)
            createErrorResult(appError.message, appError.type)
        }
    }

    private suspend fun performSearch(searchType: String, searchValue: String, level: String, year: String): List<ResultData> {
        return when (searchType) {
            "name" -> {
                val request = SearchByNameRequest(
                    search_term = searchValue,
                    level_filter = level,
                    year_filter = year.toInt()
                )
                apiService.searchByStudentName(request = request)
            }
            "number" -> {
                val request = SearchByCenterNumberRequest(
                    center_num = searchValue,
                    level_filter = level,
                    year_filter = year.toInt()
                )
                apiService.searchByCenterNumber(request = request)
            }
            "school" -> {
                val request = SearchByCenterNameRequest(
                    search_term = searchValue,
                    level_filter = level,
                    year_filter = year.toInt()
                )
                apiService.searchByCenterName(request = request)
            }
            else -> throw IllegalArgumentException("Invalid search type: $searchType")
        }
    }

    private fun applyPapersFilter(results: List<ResultData>, papersPassedFilter: String?): List<ResultData> {
        return if (papersPassedFilter != null && papersPassedFilter.isNotEmpty()) {
            val minPapers = papersPassedFilter.toIntOrNull() ?: 0
            results.filter { result ->
                val papersPassed = result.papers_passed?.toIntOrNull() ?: 0
                papersPassed >= minPapers
            }
        } else {
            results
        }
    }

    private fun validateInput(filters: HashMap<String, String>): AppError? {
        val level = filters["level"]
        val year = filters["year"]
        val searchValue = getSearchValue(filters)

        return when {
            level.isNullOrBlank() -> AppError(ErrorType.VALIDATION_ERROR, "Please select a level")
            year.isNullOrBlank() -> AppError(ErrorType.VALIDATION_ERROR, "Please enter a year")
            searchValue.isBlank() -> AppError(ErrorType.VALIDATION_ERROR, "Please enter a search term")
            year.toIntOrNull() == null -> AppError(ErrorType.VALIDATION_ERROR, "Please enter a valid year")
            year.toInt() < 2000 || year.toInt() > 2030 -> AppError(ErrorType.VALIDATION_ERROR, "Please enter a valid year (2000-2030)")
            searchValue.length < 2 -> AppError(ErrorType.VALIDATION_ERROR, "Search term must be at least 2 characters long")
            else -> null
        }
    }

    private fun mapExceptionToAppError(exception: Exception): AppError {
        return when (exception) {
            is UnknownHostException -> AppError(
                ErrorType.NETWORK_ERROR,
                "No internet connection. Please check your network settings and try again.",
                exception.message
            )
            is SocketTimeoutException -> AppError(
                ErrorType.TIMEOUT_ERROR,
                "Request timed out. Please check your connection and try again.",
                exception.message
            )
            is IOException -> AppError(
                ErrorType.NETWORK_ERROR,
                "Network error occurred. Please try again.",
                exception.message
            )
            is HttpException -> {
                when (exception.code()) {
                    401, 403 -> AppError(
                        ErrorType.AUTHENTICATION_ERROR,
                        "Authentication failed. Please contact support.",
                        "HTTP ${exception.code()}: ${exception.message()}"
                    )
                    404 -> AppError(
                        ErrorType.SERVER_ERROR,
                        "Service temporarily unavailable. Please try again later.",
                        "HTTP ${exception.code()}: ${exception.message()}"
                    )
                    500, 502, 503 -> AppError(
                        ErrorType.SERVER_ERROR,
                        "Server error occurred. Please try again later.",
                        "HTTP ${exception.code()}: ${exception.message()}"
                    )
                    else -> AppError(
                        ErrorType.SERVER_ERROR,
                        "Server error occurred. Please try again.",
                        "HTTP ${exception.code()}: ${exception.message()}"
                    )
                }
            }
            is IllegalArgumentException -> AppError(
                ErrorType.VALIDATION_ERROR,
                exception.message ?: "Invalid input provided",
                exception.message
            )
            else -> AppError(
                ErrorType.UNKNOWN_ERROR,
                "An unexpected error occurred. Please try again.",
                exception.message
            )
        }
    }

    private fun determineSearchType(filters: HashMap<String, String>): String {
        val opt = filters["opt"]
        return when (opt) {
            "2" -> "number"        // NumberFragment - search by center number
            "6" -> "name"          // VerificationFragment - search by name
            "7" -> "name"          // NameFragment - search by student name
            "8" -> "school"        // CenterFragment - search by center name
            else -> "name"         // default fallback
        }
    }

    private fun getSearchValue(filters: HashMap<String, String>): String {
        val opt = filters["opt"]
        return when (opt) {
            "2" -> filters["center_number"] ?: ""  // NumberFragment
            "6", "7" -> filters["student_name"] ?: ""   // VerificationFragment, NameFragment
            "8" -> filters["center_name"] ?: ""     // CenterFragment
            else -> filters["student_name"] ?: ""   // default
        }
    }

    private fun createErrorResult(message: String, errorType: ErrorType = ErrorType.UNKNOWN_ERROR): Results {
        return Results(
            status = -1,
            data = null,
            count = 0,
            msg = message
        )
    }
}

object ResultsApi {
    val retrofitService : ResultsApiService by lazy { retrofit.create(ResultsApiService::class.java) }
    val resultsService : ResultsService by lazy { ResultsService(retrofitService) }
}
