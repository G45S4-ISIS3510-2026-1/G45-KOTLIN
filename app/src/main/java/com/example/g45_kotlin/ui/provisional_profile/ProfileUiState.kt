package com.example.g45_kotlin.ui.provisional_profile

import com.example.g45_kotlin.data.reservation.SessionDto

data class ProfileUiState (
    val isLoading:Boolean=false,
    val numSessions:Int=0,
    val sessions: MutableSet<SessionDto> = mutableSetOf(),
    val error:String=""
)