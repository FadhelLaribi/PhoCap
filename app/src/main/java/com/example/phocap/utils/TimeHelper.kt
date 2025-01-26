package com.example.phocap.utils

import java.time.LocalDateTime

interface TimeHelper {

    enum class DateFormat(val value: String) {
        // Other needed format can be added here
        DayMonthYear("MM/dd/yyyy")
    }

    fun utcNow(): LocalDateTime
    fun toUtcEpochSecond(dateTime: LocalDateTime): Long
    fun utcNowEpochSecond(): Long
    fun getElapsedEpochMinutesTimeFrom(start: Long, end: Long): Long
    fun timeMillisToUtcEpochSeconds(time: Long): Long
    fun formatDate(format: DateFormat, date: Long): String
}