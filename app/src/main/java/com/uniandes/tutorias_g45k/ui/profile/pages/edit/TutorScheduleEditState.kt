package com.uniandes.tutorias_g45k.ui.profile.pages.edit

import com.uniandes.tutorias_g45k.ui.tutor.become.TimeSlot

data class TutorScheduleEditState (
    val availability: Map<String, List<TimeSlot>> = emptyMap(),
    val selectedDay: String = "LUN",
    val isLoading: Boolean = false,
    val error: String? = null
)
