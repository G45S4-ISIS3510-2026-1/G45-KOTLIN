package com.uniandes.tutorias_g45k.data.profile

import com.google.gson.annotations.SerializedName

data class CreatePqrRequest(
    @SerializedName("type") val type: String,
    @SerializedName("topic") val topic: String,
    @SerializedName("description") val description: String,
    @SerializedName("authorId") val authorId: String,
    @SerializedName("relatedIncident") val relatedIncident: String? = null,
    @SerializedName("status") val status: String = "Pendiente"
)

data class PqrResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("type") val type: String,
    @SerializedName("topic") val topic: String,
    @SerializedName("description") val description: String,
    @SerializedName("authorId") val authorId: String,
    @SerializedName("relatedIncident") val relatedIncident: String?,
    @SerializedName("status") val status: String,
    @SerializedName("createdAt") val createdAt: String?
)
