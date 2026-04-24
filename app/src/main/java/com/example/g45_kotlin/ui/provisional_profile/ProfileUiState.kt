package com.uniandes.tutorias_g45k.ui.provisional_profile

import com.uniandes.tutorias_g45k.data.reservation.SessionDto

data class ProfileUiState (
    val isLoading:Boolean=false,
    val numSessions:Int=0,
    val sessions: MutableSet<SessionDto> = mutableSetOf(),
    val error:String="",
    val connected:Boolean=true
)
