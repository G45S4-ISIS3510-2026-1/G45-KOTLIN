package com.example.g45_kotlin.ui.profile.pages.reviews

import com.example.g45_kotlin.data.reservation.SessionDto

data class ReservationListUiState (
    val isLoading:Boolean=false,
    val numSessions:Int=0,
    val sessions: List<SessionDto> = emptyList(),
    val error:String="",
    val connected:Boolean=true
)