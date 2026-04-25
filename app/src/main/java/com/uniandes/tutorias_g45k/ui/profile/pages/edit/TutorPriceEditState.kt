package com.uniandes.tutorias_g45k.ui.profile.pages.edit

data class TutorPriceEditState (
    val sessionPrice: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)
