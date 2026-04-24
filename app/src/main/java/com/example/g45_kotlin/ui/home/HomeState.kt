package com.uniandes.tutorias_g45k.ui.home

import com.uniandes.tutorias_g45k.data.reservation.SessionDto
import com.uniandes.tutorias_g45k.data.user.TutorSummaryDto

data class HomeState(
    val userName: String = "Amigo",
    val nextSessions: List<SessionDto> = emptyList(),
    val sessionError: String? = null,
    val featuredTutors: List<TutorSummaryDto> = emptyList(),
    val isLoading: Boolean = false,
    val areSessionLoading: Boolean = false,
    val error: String? = null
)

