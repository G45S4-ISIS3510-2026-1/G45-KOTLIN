package com.example.g45_kotlin.ui.tutor.catalog

import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName

data class CatalogoUiState(
    val searchText: String = "",
    val tutores: List<Tutor> = emptyList(),
    val filtrados: List<Tutor> = emptyList(),
    val selectedOrder: String = "Mejor Rating",
    val selectedFacultad: String = "Todas",
    val selectedTutor: Tutor? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

// DTO para la respuesta del listado y detalle
data class TutorResponse(
    val id: String,
    val name: String,
    val email: String,
    val major: String,
    val isTutoring: Boolean,
    val sessionPrice: Int,
    val profileImageUrl: String?,
    @SerializedName("tutoringSkills") val tutoringSkills: List<String> = emptyList(),
    val bio: String? = null,
    @SerializedName("average_rating") val averageRating: Float? = null
)

data class ReviewResponse(
    val id: String,
    @SerializedName("label") val label: String,
    @SerializedName("rating") val rating: Float,
    @SerializedName("details") val details: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("authorId") val authorId: String
)

data class Tutor(
    val id: String,
    val nombre: String,
    val carrera: String,
    val facultad: String,
    val tags: List<String>,
    val precio: String,
    val precioValor: Int,
    val rating: String,
    val colorAvatar: Color,
    val descripcion: String = "Estudiante destacado con amplia experiencia en las materias mencionadas.",
    val numTutorias: String = "+120",
    val nivel: String = "Senior",
    val email: String = "c.rivas@uniandes.edu.co",
    val telefono: String = "+57 300 000 0000",
    val reseñas: List<Reseña> = emptyList()
)

data class Reseña(
    val autor: String,
    val fecha: String,
    val estrellas: Int,
    val comentario: String
)
