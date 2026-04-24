package com.uniandes.tutorias_g45k.ui.profile.pages.reservations

import com.uniandes.tutorias_g45k.data.reservation.SessionDto

data class ReviewListUiState (
    val isLoading:Boolean=false,
    val numSessions:Int=0,
    val sessions: List<SessionDto> = emptyList(),
    val error:String="",
    val connected:Boolean=true
)