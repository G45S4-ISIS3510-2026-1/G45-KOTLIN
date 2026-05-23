package com.uniandes.tutorias_g45k.data.profile

import com.google.gson.annotations.SerializedName

data class CreatePqrRequest(
    @SerializedName("type") val type: String,
    @SerializedName("topic") val topic: String,
    @SerializedName("description") val description: String,
    @SerializedName("author_id") val authorId: String,
    @SerializedName("related_incident") val relatedIncident: String? = null,
    @SerializedName("status") val status: String = "Pendiente"
)

data class PqrResponse(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("topic") val topic: String,
    @SerializedName("description") val description: String,
    @SerializedName("author_id") val authorId: String,
    @SerializedName("related_incident") val relatedIncident: String?,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String
)
