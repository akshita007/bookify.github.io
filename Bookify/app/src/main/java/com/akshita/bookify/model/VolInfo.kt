package com.akshita.bookify.model

import org.json.JSONArray

data class VolInfo(
    val id:String,
    val title:String,
    val authors: JSONArray,
    val publisher:String,
    val description:String,
    val pageCount:Int?,
    val categories: JSONArray,
    val averageRating:Double?,
    val thumbnail: String,
    val language:String,
    val amount: Double,
    val buyLink:String,
)