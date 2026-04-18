package com.example.g45_kotlin.data.novelty

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

enum class NoveltyType (val label: String) {
    SESSION("sesion"),
    PRICE_CHANGE("precio_cambiado"),
    INCOMING_SESION("nueva_sesion"),
    NEW_REVIEW("review");


    companion object {
        fun fromLabel(label: String): NoveltyType? {
            return entries.find { it.label == label }
        }
    }
}


data class NoveltyDto (
    @DocumentId
    val id: String="",
    val title: String="",
    val userId: String="",
    val createdAt: Timestamp=Timestamp.now(),
    val isRead:Boolean=false,
    val type: String="",
    val description : String="",
    val entityId:String=""
){
    val noveltyType: NoveltyType?
        get() = NoveltyType.fromLabel(type)
}