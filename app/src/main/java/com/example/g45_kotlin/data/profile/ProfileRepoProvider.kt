package com.example.g45_kotlin.data.profile

object ProfileRepoProvider {
    fun getRepository():ProfileRepository {
        return ProfileRepoFirestoreImp
    }
}