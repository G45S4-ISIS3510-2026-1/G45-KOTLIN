package com.uniandes.tutorias_g45k.ui.reservation.gateway

import com.uniandes.tutorias_g45k.data.reservation.SkillSummaryDto
import com.uniandes.tutorias_g45k.utilities.getDaysOfCurrentReservationWeek
import java.time.LocalDate


data class ReservationGatewayState (
    val isLoading : Boolean = true,
    val selectedDate : LocalDate = LocalDate.now(),
    val selectedHour : String = "",
    val selectedPaymentType : PaymentType = PaymentType.RECEIPT,
    val selectedPaymentMethod : PaymentMethod = PaymentMethod(),
    val selectedSkill : SkillSummaryDto = SkillSummaryDto(null, label=""),
    val sessionTutor: TutorUser = TutorUser(),
    val tutorSkills: List<SkillSummaryDto> = emptyList(),
    val error:String = "",
    val hours:List<String> = emptyList(),
    val dates:List<LocalDate> = getDaysOfCurrentReservationWeek()
)

data class PaymentMethod (
    val holder:String  = "",
    val number:String  = "",
    val expirationDate:String = "",
    val cvv:Int=0
)

data class TutorUser (
    val name:String = "",
    val major:String = "Música",
    val skills:List<String> = emptyList(),
    val sessionPrice:Int = 0,
    val currentRating:Double = 0.0,
    val picture: String=""
)

