package com.uniandes.tutorias_g45k.data.profile

object ProfileRepoProvider {
    fun getRepository():ProfileRepository {
        return ProfileRepoFirestoreImp
    }
}