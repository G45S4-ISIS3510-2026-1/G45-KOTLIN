package com.example.g45_kotlin.ui.tutor.catalog

import androidx.compose.ui.graphics.Color

class TutorRepository(private val apiService: ApiService = RetrofitClient.apiService) {
    
    suspend fun searchTutors(name: String? = null, major: String? = null): Result<List<Tutor>> {
        return try {
            val response = apiService.searchTutors(name, major)
            val tutores = response.map { res ->
                Tutor(
                    id = res.id,
                    nombre = res.name,
                    carrera = res.major,
                    facultad = mapearFacultad(res.major),
                    tags = res.tutoringSkills,
                    precio = "$${res.sessionPrice / 1000}k/h",
                    precioValor = res.sessionPrice,
                    rating = res.averageRating?.toString() ?: "4.8",
                    colorAvatar = Color.Gray,
                    email = res.email,
                    descripcion = res.bio ?: "Estudiante destacado con amplia experiencia en las materias mencionadas."
                )
            }
            Result.success(tutores)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTutorDetail(userId: String): Result<Tutor> {
        return try {
            val res = apiService.getTutorDetail(userId)
            
            val reviewsRes = try {
                apiService.getTutorReviews(userId)
            } catch (e: Exception) {
                emptyList()
            }
            
            val tutor = Tutor(
                id = res.id,
                nombre = res.name,
                carrera = res.major,
                facultad = mapearFacultad(res.major),
                tags = res.tutoringSkills,
                precio = "$${res.sessionPrice / 1000}k/h",
                precioValor = res.sessionPrice,
                rating = res.averageRating?.toString() ?: "4.8",
                colorAvatar = Color.Gray,
                descripcion = res.bio ?: "Sin descripción.",
                email = res.email,
                reseñas = reviewsRes.map { rev ->
                    Reseña(
                        autor = "Usuario",
                        fecha = formatearFecha(rev.createdAt),
                        estrellas = rev.rating.toInt(),
                        comentario = "${rev.label}: ${rev.details}"
                    )
                }
            )
            Result.success(tutor)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapearFacultad(major: String): String {
        return when {
            major.contains("Ingeniería", ignoreCase = true) -> "Ingeniería"
            major.contains("Matemáticas", ignoreCase = true) || major.contains("Física", ignoreCase = true) -> "Ciencias"
            major.contains("Economía", ignoreCase = true) || major.contains("Administración", ignoreCase = true) -> "Economía"
            major.contains("Artes", ignoreCase = true) || major.contains("Diseño", ignoreCase = true) -> "Artes"
            else -> "Otras"
        }
    }

    private fun formatearFecha(isoDate: String): String {
        return isoDate.split("T").firstOrNull() ?: "Reciente"
    }
}
