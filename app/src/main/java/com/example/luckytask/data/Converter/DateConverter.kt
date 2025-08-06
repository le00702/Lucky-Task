package com.example.luckytask.data.Converter

import androidx.room.TypeConverter
import java.time.LocalDate

/**
 * Room TypeConverters to handle LocalDate
 */

class DateConverter {

    /**
     * Convert a Long (epoch_day) from the database to a LocalDate object.
     *
     * @param epoch_day: Number of days since the Unix epoch (1970-01-01)
     * @return Corresponding LocalDate, or null if input is null
     */
    @TypeConverter
    fun fromEpochDay(epoch_day: Long?): LocalDate? {
        return epoch_day?.let { LocalDate.ofEpochDay(it) }
    }


    /**
     * Convert a LocalDate to a Long (epoch day) for database storage.
     *
     * @param date: LocalDate to convert
     * @return Number of days since the Unix epoch, or null if date is null
     */
    @TypeConverter
    fun toEpochDay(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}