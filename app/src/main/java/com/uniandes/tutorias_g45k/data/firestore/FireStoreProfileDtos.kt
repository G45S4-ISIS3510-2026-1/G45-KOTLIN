package com.uniandes.tutorias_g45k.data.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

data class FirestoreReviewDto (
    val id:String="",
    val label:String="",
    val details:String="",
    val rating:Double=0.0,
    val authorId:String="",
    val tutorId:String="",
    val createdAt: Timestamp =Timestamp.now()
)

data class FirestorePqrDto (
    val id:String="",
    val type:String?="",
    val description:String?="",
    val topic:String?="",
    val status:String?="",
    val authorId:String?="",
    val relatedIncident:String?="",
    val createdAt: Timestamp? = Timestamp.now()
)

@IgnoreExtraProperties
data class FirestoreUserSummaryDto (
    val id:String="",
    val name:String="",
    val major:String="",
    val uniandesId:Long?=null,
    val profileImageUrl:String="https://media.licdn.com/dms/image/v2/D4E03AQErWMZGSWwcqg/profile-displayphoto-crop_800_800/B4EZxJq0pSLAAI-/0/1770762490962?e=1778716800&v=beta&t=khP8DFqey-djTQzSuecIN4GTT-JnPEjWwDdBxAPyrls",
    val sessionPrice:Int?=null,
    val tutorRating:Double?=null,
    @get:PropertyName("isTutoring")
    @PropertyName("isTutoring")
    val isTutoring:Boolean=false
)
