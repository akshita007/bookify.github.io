package com.akshita.bookify.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Book")
data class BookEntities(
    @ColumnInfo(name="book_id")@PrimaryKey val id:String,
    @ColumnInfo(name="book_name") val title:String,
    @ColumnInfo(name = "book_author") val authors: String,
    @ColumnInfo(name = "book_publisher") val publisher: String,
    @ColumnInfo(name = "book_desc") val description:String,
    @ColumnInfo(name = "book_pageCount") val pageCount: Int?,
    @ColumnInfo(name = "book_category") val categories: String,
    @ColumnInfo(name = "book_rating") val averageRating: Double?,
    @ColumnInfo(name = "book_image") val thumbnail:String,
    @ColumnInfo(name = "book_language") val language: String,
    @ColumnInfo(name = "book_cost") val amount:Double,
    @ColumnInfo(name = "book_buyLink") val buyLink: String?,
    )