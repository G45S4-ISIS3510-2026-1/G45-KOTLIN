package com.example.g45_kotlin.ui.reservation.gateway

import java.time.LocalDate


data class ReservationGatewayState (
    val selectedDate : LocalDate = LocalDate.now(),
    val selectedHour : String = "",
    val selectedPaymentType : PaymentType = PaymentType.RECEIPT,
    val selectedPaymentMethod : PaymentMethod = PaymentMethod()
)

data class PaymentMethod (
    val holder:String  = "",
    val number:String  = "",
    val expirationDate:String ? = "",
    val cvv:Int=0
)

