/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.camgist.gceresults.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * This data class defines a Mars property which includes an ID, the image URL, the type (sale
 * or rental) and the price (monthly if it's a rental).
 * The property names of this data class are used by Moshi to match the names of values in JSON.
 */
//data class MarsProperty(
//        val id: String,
//        // used to map img_src from the JSON to imgSrcUrl in our class
//        @Json(name = "img_src") val imgSrcUrl: String,
//        val type: String,
//        val price: Double)

// RPC function parameter data classes
data class SearchByNameRequest(
    val search_term: String,
    val level_filter: String,
    val year_filter: Int
)

data class SearchByCenterNumberRequest(
    val center_num: String,
    val level_filter: String,
    val year_filter: Int
)

data class SearchByCenterNameRequest(
    val search_term: String,
    val level_filter: String,
    val year_filter: Int
)

// New API Request format
data class SearchRequest(
    val searchType: String,
    val searchValue: String,
    val level: String,
    val year: String
)

// New API Response format (matches website)
data class SearchResponse(
    val results: List<ResultData>,
    val totalResults: Int
)

// Legacy Results class (for backward compatibility)
@Parcelize
data class Results(
        val status : Int,
        val data : List<ResultData>?,
        val count : Int,
        val msg: String
        ) : Parcelable


@Parcelize
data class ResultData(
        val record_id : Int,
        val center_name: String,
        val center_number: String,
        val level: String,
        val year: Int,
        val papers_passed: String?,
        val student_name: String,
        val student_grades: String?,
        val entry_date : String
) : Parcelable

// New metadata data classes
data class LevelData(
    val level_code: String,
    val level_name: String
)

data class YearData(
    val year: Int
)

data class MetadataResponse(
    val levels: List<LevelData>?,
    val years: List<YearData>?
)










