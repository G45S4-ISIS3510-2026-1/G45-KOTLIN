package com.uniandes.tutorias_g45k.ui.novelties

import com.uniandes.tutorias_g45k.data.novelty.NoveltyDto

data class NoveltyScreenState (
    val novelties: List<NoveltyDto> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
