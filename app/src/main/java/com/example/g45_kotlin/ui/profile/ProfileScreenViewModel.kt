package com.example.g45_kotlin.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g45_kotlin.data.auth.AuthHolder
import com.example.g45_kotlin.data.profile.ProfileRepoProvider
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
        _uiState.update { it.copy(isLoading = true, error = "")  }
        viewModelScope.launch(Dispatchers.IO) {
            val result=repo.getProfile(authRepo.getCurrentUser()?.uid ?: "")
            withContext(Dispatchers.Main) {
                if (result.isSuccess){
                    _uiState.update { it.copy(user = result.getOrNull(), isLoading = false, error = "") }
                }else{
                    _uiState.update { it.copy(error = result.exceptionOrNull()?.message ?: "Error al obtener el perfil", isLoading = false) }
                }
            }
        }
    }

    init{
        fetchProfile()
    }
}
