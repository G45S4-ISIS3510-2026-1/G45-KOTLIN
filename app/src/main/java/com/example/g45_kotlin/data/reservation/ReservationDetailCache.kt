package com.example.g45_kotlin.data.reservation

import java.time.LocalDateTime

data class ReservationDetailCache(
    var reservation: SessionDto,
    var lastUpdated: LocalDateTime=LocalDateTime.now(),
    var isStale:Boolean
)