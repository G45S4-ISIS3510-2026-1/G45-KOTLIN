package com.uniandes.tutorias_g45k.data.reservation

import java.time.LocalDateTime

data class ReservationDetailCache(
    var reservation: SessionDto,
    var lastUpdated: LocalDateTime=LocalDateTime.now(),
    var isStale:Boolean
)