package com.example.g45_kotlin.ui.reservation.gateway

import java.time.LocalDate


data class ReservationGatewayState (
    val isLoading : Boolean = false,
    val selectedDate : LocalDate = LocalDate.now(),
    val selectedHour : String = "",
    val selectedPaymentType : PaymentType = PaymentType.RECEIPT,
    val selectedPaymentMethod : PaymentMethod = PaymentMethod(),
    val sessionTutor: TutorUser = TutorUser()
)

data class PaymentMethod (
    val holder:String  = "",
    val number:String  = "",
    val expirationDate:String = "",
    val cvv:Int=0
)

data class TutorUser (
    val name:String = "",
    val skills:Set<String> = setOf(),
    val sessionPrice:Double = 0.0,
    val currentRating:Double = 0.0,
    val picture: String=""
)

