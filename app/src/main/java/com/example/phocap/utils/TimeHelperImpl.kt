package com.example.phocap.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class TimeHelperImpl @Inject constructor() : TimeHelper {

    override fun utcNow(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

    override fun toUtcEpochSecond(dateTime: LocalDateTime) = dateTime.toEpochSecond(ZoneOffset.UTC)

    override fun utcNowEpochSecond() = toUtcEpochSecond(utcNow())

    override fun getElapsedEpochMinutesTimeFrom(start: Long, end: Long) = (end - start) / 60

    override fun timeMillisToUtcEpochSeconds(time: Long) =
        Instant.ofEpochMilli(time).atZone(ZoneOffset.UTC).toEpochSecond()

    override fun formatDate(format: TimeHelper.DateFormat, date: Long): String {
        val formatter = SimpleDateFormat(format.value, Locale.getDefault())
        return formatter.format(Date(date))
    }
}