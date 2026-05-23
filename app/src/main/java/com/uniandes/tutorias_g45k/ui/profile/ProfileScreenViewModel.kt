package com.uniandes.tutorias_g45k.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.firestore.FirestoreUserSummaryDto
import com.uniandes.tutorias_g45k.data.profile.ProfileRepoProvider
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileScreenViewModel(): ViewModel()  {
    private val _uiState = MutableStateFlow(ProfileScreenState())
    private val repo= ProfileRepoProvider.getRepository()
    private val authRepo= AuthHolder.authRepo
    val uiState: StateFlow<ProfileScreenState> = _uiState.asStateFlow()

    fun fetchProfile() {
        Log.d("ProfileScreenViewModel", "Fetching profile...")
        _uiState.update { it.copy(isLoading = true, error = "")  }
        viewModelScope.launch(Dispatchers.IO) {
            try{
                if(!NetworkMonitor.isOnline.value){
                    val userBack=authRepo.getLocalUser()
                    withContext(Dispatchers.Main) {
                        val user= FirestoreUserSummaryDto(
                            id = userBack?.uid ?: "",
                            name = userBack?.displayName ?: "",
                            profileImageUrl = userBack?.photoUrl?: "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png?_=20150327203541",
                            major = userBack?.major ?: "",
                            tutorRating = userBack?.tutorRating ?: 0.0,
                            isTutoring = userBack?.isTutoring ?: false,
                            uniandesId = userBack?.uniandesId?.toLong() ?: 0
                        )
                        _uiState.update { it.copy(user = user,
                            isLoading = false, error = "") }
                    }
                    return@launch
                }
                val result=repo.getProfile(authRepo.getCurrentUser()?.uid ?: "")
                Log.d("ProfileScreenViewModel", "Profile fetched: ${result.getOrNull()}")
                withContext(Dispatchers.Main) {
                    if (result.isSuccess){
                        _uiState.update { it.copy(user = result.getOrNull(), isLoading = false, error = "") }
                        Log.d("ProfileScreenViewModel", "Profile fetched: ${result.getOrNull()}")
                    }else{
                        val userBack=authRepo.getLocalUser()
                        withContext(Dispatchers.Main) {
                            val user= FirestoreUserSummaryDto(
                                id = userBack?.uid ?: "",
                                name = userBack?.displayName ?: "",
                                profileImageUrl = userBack?.photoUrl?: "",
                                major = userBack?.major ?: "",
                                tutorRating = userBack?.tutorRating ?: 0.0,
                                isTutoring = userBack?.isTutoring ?: false
                            )
                            _uiState.update { it.copy(user = user,
                                isLoading = false, error = "") }
                        }
                        _uiState.update { it.copy(error = result.exceptionOrNull()?.message ?: "Error al obtener el perfil", isLoading = false) }
                    }
                }
            }catch (e:Exception){
                val provisionalUser=authRepo.getCurrentUser()
                val user= FirestoreUserSummaryDto(
                    id = provisionalUser?.uid ?: "",
                    name = provisionalUser?.displayName ?: "",
                    profileImageUrl = provisionalUser?.photoUrl?: "")
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(error = e.message ?: "Error al obtener el perfil", isLoading = false, user = user) }
                }
            }

        }
    }

    init{
        fetchProfile()
    }



    fun clearMissings(){
        _uiState.update { it.copy(clickedMissings = false) }
    }

    fun setMissings(error:String="Vista no disponible o en mantenimiento, intenta mas tarde"){
        _uiState.update { it.copy(clickedMissings = true, error = error) }
    }
}
