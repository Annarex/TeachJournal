
package com.donntu.teachjournal.db.utils

import androidx.room.TypeConverter
import java.sql.Date

public class Converters {
    @TypeConverter
    fun fromTimestamp( value: Long?) :
            java.sql.Date {
        return java.sql.Date(value ?: 0)
    }
    @TypeConverter
    fun dateToTimestamp(date :java.sql.Date?)
            :Long {
        return date?.getTime() ?: 0
    }}