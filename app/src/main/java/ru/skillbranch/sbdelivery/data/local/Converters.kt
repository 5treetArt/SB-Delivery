package ru.skillbranch.sbdelivery.data.local

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun timestampToDate(timestamp: Long): Date = Date(timestamp)

    @TypeConverter
    fun dateToTimestamp(date: Date): Long = date.time
}