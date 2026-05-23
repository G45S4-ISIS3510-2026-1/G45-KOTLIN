package com.uniandes.tutorias_g45k.ui.profile.pages.pqrs

import com.uniandes.tutorias_g45k.data.reservation.SessionDto

data class PqrState(
    val type: String = "Petición",
    val selectedReason: String = "",
    val description: String = "",
    val sessions: List<SessionDto> = emptyList(),
    val selectedSessionId: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

val pqrTypes = listOf("Petición", "Queja", "Reclamo")

val pqrReasons = listOf(
    "Problemas con el tutor",
    "Problemas con el pago",
    "Problemas técnicos",
    "Sugerencia",
    "Otro"
)
