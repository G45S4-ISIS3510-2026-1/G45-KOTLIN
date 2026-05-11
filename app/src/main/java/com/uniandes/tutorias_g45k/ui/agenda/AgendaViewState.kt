package com.uniandes.tutorias_g45k.ui.agenda

import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.reservation.SessionDto
import java.time.LocalDate

data class AgendaViewState (
    val isLoading: Boolean = true,
    val selectedWeek: List<LocalDate> = emptyList(),
    val selectedDay: LocalDate = LocalDate.now(),
    val sessions: List<SessionDto> = emptyList(),
    val currentUserId: String?= AuthHolder.authRepo.getCurrentUserId()

    )