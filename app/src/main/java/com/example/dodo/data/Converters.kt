package com.example.dodo.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun fromEpochDay(epochDay: Long?): LocalDate? {
        return epochDay?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun toEpochDay(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun fromStatusName(name: String?): EntryStatus? {
        return name?.let { EntryStatus.valueOf(it) }
    }

    @TypeConverter
    fun toStatusName(status: EntryStatus?): String? {
        return status?.name
    }
}