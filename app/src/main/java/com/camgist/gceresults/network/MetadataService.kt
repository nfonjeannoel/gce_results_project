package com.camgist.gceresults.network

import android.content.Context
import android.util.Log
import com.camgist.gceresults.utils.MetadataCacheManager
import com.camgist.gceresults.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MetadataService(private val context: Context) {
    
    private val cacheManager = MetadataCacheManager(context)
    private val apiService = ResultsApi.retrofitService
    
    /**
     * Get levels - tries cache first, then API if needed
     */
    suspend fun getLevels(): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                // First try to get from cache
                val cachedLevels = cacheManager.getCachedLevels()
                if (cachedLevels != null) {
                    Log.d("MetadataService", "Retrieved ${cachedLevels.size} levels from cache")
                    return@withContext cachedLevels.map { it.level_name }
                }
                
                // If no cache and network available, fetch from API
                if (NetworkUtils.isNetworkAvailable(context)) {
                    Log.d("MetadataService", "Cache miss, fetching levels from API")
                    val levels = apiService.getLevels()
                    cacheManager.cacheLevels(levels)
                    Log.d("MetadataService", "Cached ${levels.size} levels from API")
                    return@withContext levels.map { it.level_name }
                }
                
                // Fallback to cache manager's default values
                Log.d("MetadataService", "No network, using fallback levels")
                cacheManager.getLevelsForDisplay()
                
            } catch (e: Exception) {
                Log.e("MetadataService", "Error fetching levels", e)
                // Return fallback values
                cacheManager.getLevelsForDisplay()
            }
        }
    }
    
    /**
     * Get years - tries cache first, then API if needed
     */
    suspend fun getYears(): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                // First try to get from cache
                val cachedYears = cacheManager.getCachedYears()
                if (cachedYears != null) {
                    Log.d("MetadataService", "Retrieved ${cachedYears.size} years from cache")
                    return@withContext cachedYears.map { it.year.toString() }.sortedDescending()
                }
                
                // If no cache and network available, fetch from API
                if (NetworkUtils.isNetworkAvailable(context)) {
                    Log.d("MetadataService", "Cache miss, fetching years from API")
                    val years = apiService.getYears()
                    cacheManager.cacheYears(years)
                    Log.d("MetadataService", "Cached ${years.size} years from API")
                    return@withContext years.map { it.year.toString() }.sortedDescending()
                }
                
                // Fallback to cache manager's default values
                Log.d("MetadataService", "No network, using fallback years")
                cacheManager.getYearsForDisplay()
                
            } catch (e: Exception) {
                Log.e("MetadataService", "Error fetching years", e)
                // Return fallback values
                cacheManager.getYearsForDisplay()
            }
        }
    }
    
    /**
     * Convert display level name to level code for API calls
     */
    fun getLevelCodeFromDisplayName(displayName: String): String {
        return cacheManager.getLevelCodeFromDisplayName(displayName)
    }
    
    /**
     * Force refresh metadata from API
     */
    suspend fun refreshMetadata(): Pair<Boolean, String> {
        return withContext(Dispatchers.IO) {
            try {
                if (!NetworkUtils.isNetworkAvailable(context)) {
                    return@withContext Pair(false, "No internet connection available")
                }
                
                // Fetch both levels and years
                val levels = apiService.getLevels()
                val years = apiService.getYears()
                
                // Cache the results
                cacheManager.cacheLevels(levels)
                cacheManager.cacheYears(years)
                
                Log.d("MetadataService", "Refreshed metadata: ${levels.size} levels, ${years.size} years")
                Pair(true, "Metadata updated successfully")
                
            } catch (e: Exception) {
                Log.e("MetadataService", "Error refreshing metadata", e)
                Pair(false, "Failed to refresh metadata: ${e.message}")
            }
        }
    }
    
    /**
     * Clear cached metadata
     */
    fun clearCache() {
        cacheManager.clearCache()
        Log.d("MetadataService", "Metadata cache cleared")
    }
} 