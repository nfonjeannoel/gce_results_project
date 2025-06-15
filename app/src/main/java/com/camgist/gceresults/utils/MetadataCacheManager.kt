package com.camgist.gceresults.utils

import android.content.Context
import android.content.SharedPreferences
import com.camgist.gceresults.network.LevelData
import com.camgist.gceresults.network.YearData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MetadataCacheManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "gce_metadata_cache", 
        Context.MODE_PRIVATE
    )
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    private val levelsAdapter: JsonAdapter<List<LevelData>> = moshi.adapter(
        Types.newParameterizedType(List::class.java, LevelData::class.java)
    )
    
    private val yearsAdapter: JsonAdapter<List<YearData>> = moshi.adapter(
        Types.newParameterizedType(List::class.java, YearData::class.java)
    )
    
    companion object {
        private const val KEY_LEVELS = "cached_levels"
        private const val KEY_YEARS = "cached_years"
        private const val KEY_LEVELS_TIMESTAMP = "levels_timestamp"
        private const val KEY_YEARS_TIMESTAMP = "years_timestamp"
        private const val CACHE_VALIDITY_HOURS = 24 // Cache valid for 24 hours
    }
    
    /**
     * Cache levels data
     */
    fun cacheLevels(levels: List<LevelData>) {
        val levelsJson = levelsAdapter.toJson(levels)
        prefs.edit()
            .putString(KEY_LEVELS, levelsJson)
            .putLong(KEY_LEVELS_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }
    
    /**
     * Cache years data
     */
    fun cacheYears(years: List<YearData>) {
        val yearsJson = yearsAdapter.toJson(years)
        prefs.edit()
            .putString(KEY_YEARS, yearsJson)
            .putLong(KEY_YEARS_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }
    
    /**
     * Get cached levels if valid
     */
    fun getCachedLevels(): List<LevelData>? {
        if (!isLevelsCacheValid()) return null
        
        val levelsJson = prefs.getString(KEY_LEVELS, null) ?: return null
        return try {
            levelsAdapter.fromJson(levelsJson)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Get cached years if valid
     */
    fun getCachedYears(): List<YearData>? {
        if (!isYearsCacheValid()) return null
        
        val yearsJson = prefs.getString(KEY_YEARS, null) ?: return null
        return try {
            yearsAdapter.fromJson(yearsJson)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Check if levels cache is still valid
     */
    private fun isLevelsCacheValid(): Boolean {
        val timestamp = prefs.getLong(KEY_LEVELS_TIMESTAMP, 0)
        return System.currentTimeMillis() - timestamp < CACHE_VALIDITY_HOURS * 60 * 60 * 1000
    }
    
    /**
     * Check if years cache is still valid
     */
    private fun isYearsCacheValid(): Boolean {
        val timestamp = prefs.getLong(KEY_YEARS_TIMESTAMP, 0)
        return System.currentTimeMillis() - timestamp < CACHE_VALIDITY_HOURS * 60 * 60 * 1000
    }
    
    /**
     * Clear all cached metadata
     */
    fun clearCache() {
        prefs.edit()
            .remove(KEY_LEVELS)
            .remove(KEY_YEARS)
            .remove(KEY_LEVELS_TIMESTAMP)
            .remove(KEY_YEARS_TIMESTAMP)
            .apply()
    }
    
    /**
     * Get levels as display-friendly list for UI (fallback to hardcoded if no cache)
     */
    fun getLevelsForDisplay(): List<String> {
        val cachedLevels = getCachedLevels()
        return if (cachedLevels != null) {
            cachedLevels.map { it.level_name }
        } else {
            // Fallback to hardcoded levels
            listOf("A-Level General", "A-Level Technical", "O-Level General", "O-Level Technical")
        }
    }
    
    /**
     * Get years as display-friendly list for UI (fallback to hardcoded if no cache)
     */
    fun getYearsForDisplay(): List<String> {
        val cachedYears = getCachedYears()
        return if (cachedYears != null) {
            cachedYears.map { it.year.toString() }.sorted().reversed()
        } else {
            // Fallback to hardcoded years
            listOf("2024", "2023", "2022", "2021", "2020", "2019")
        }
    }
    
    /**
     * Convert display level name to level code for API calls
     */
    fun getLevelCodeFromDisplayName(displayName: String): String {
        val cachedLevels = getCachedLevels()
        if (cachedLevels != null) {
            return cachedLevels.find { it.level_name == displayName }?.level_code ?: ""
        }
        
        // Fallback to hardcoded mapping
        return when(displayName) {
            "A-Level General" -> "ALG"
            "A-Level Technical" -> "ALT"
            "O-Level General" -> "OLG"
            "O-Level Technical" -> "OLT"
            else -> ""
        }
    }
} 