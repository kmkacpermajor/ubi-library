package edu.put.inf151753

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

fun toDate(timestamp: Long): String{
    if (timestamp == 0L) return "BRAK"
    return SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date(timestamp*1000)).toString()
}
