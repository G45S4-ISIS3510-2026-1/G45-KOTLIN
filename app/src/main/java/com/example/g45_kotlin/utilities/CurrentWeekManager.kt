package com.uniandes.tutorias_g45k.utilities

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters


fun getDaysOfCurrentWeek(): List<LocalDate> {
    val today = LocalDate.now(ZoneId.of("America/Bogota"))
    val monday = if (today.dayOfWeek != DayOfWeek.SUNDAY && today.dayOfWeek != DayOfWeek.SATURDAY) today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)) else today.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
    // Entresemana mas Sabado
    return (0..5).map { monday.plusDays(it.toLong()) }
}



