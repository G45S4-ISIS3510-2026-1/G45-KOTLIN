package com.example.g45_kotlin.utilities

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters


fun getDaysOfCurrentWeek(): List<LocalDate> {
    val today = LocalDate.now(ZoneId.systemDefault())

    val monday = if (today.dayOfWeek != DayOfWeek.SUNDAY) today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)) else today.with(TemporalAdjusters.next(DayOfWeek.MONDAY))

    // Entresemana
    return (0..5).map { monday.plusDays(it.toLong()) }
}

//Temporal para despligue, tomar horas disponibles del ViewModel/API
fun getWorkingHours(): List<String> {
    val startTime = LocalTime.of(7, 0)  // 7:00 AM
    val endTime = LocalTime.of(20, 0)  // 8:00 PM
    val formatter = DateTimeFormatter.ofPattern("hh:mm a") // Formato: 07:00 AM

    val hoursList = mutableListOf<String>()
    var currentTime = startTime


    while (!currentTime.isAfter(endTime)) {
        hoursList.add(currentTime.format(formatter))
        currentTime = currentTime.plusHours(1)
    }

    return hoursList
}
