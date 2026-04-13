package com.example.g45_kotlin.ui.reservation.summary

import java.time.LocalDateTime

enum class Status (val status:String) {
    PENDING("Pendiente"),
    CONCLUDED("Concluida"),
    CANCELLED("Cancelada"),

    OVERDUE("Vencida")
}

data class UserData (
    val id:String = "123456789",
    val name : String = "<<Usuario Desconocido o Eliminado>>",
    val picture : String = "https://media.licdn.com/dms/image/v2/D4E03AQ",
)

data class ReservationSummaryState (
    val isLoading : Boolean = false,
    val id : String = "123456789",
    val status : Status = Status.PENDING,
    val date : LocalDateTime = LocalDateTime.now(),
    val skill : String = "Calculo Vectorial",
    val tutor : UserData = UserData(),
    val student : UserData = UserData(),
    val qrContent: String = "www.google.com"
)