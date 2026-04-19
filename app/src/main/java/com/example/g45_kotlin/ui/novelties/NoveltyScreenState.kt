package com.example.g45_kotlin.ui.novelties

import com.example.g45_kotlin.data.novelty.NoveltyDto

data class NoveltyScreenState (
    val novelties: List<NoveltyDto> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)