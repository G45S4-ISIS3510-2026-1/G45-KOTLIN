package com.uniandes.tutorias_g45k.ui.profile.pages.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TutorPriceEditViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(TutorPriceEditState())
    val uiState: StateFlow<TutorPriceEditState> = _uiState.asStateFlow()

    private val authRepo= AuthHolder.authRepo
    private val userRepo= UserRepository


    fun updatePrice(price: Int) {
        _uiState.update { it.copy(sessionPrice = price) }
    }

    fun publishPrice(onSucces: () -> Unit){
        val currentPrice=_uiState.value.sessionPrice
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val userId=authRepo.getCurrentUserId() ?: return@launch
                val result=userRepo.updateSessionPrice(userId, currentPrice)
                if(result.isSuccessful){
                    authRepo.updateLocalUser(authRepo.getLocalUser()?.copy(sessionPrice = currentPrice) ?: return@launch)
                    withContext(Dispatchers.Main){_uiState.update { it.copy(isLoading = false) }}
                    withContext(Dispatchers.Main){onSucces()}
                }
             }catch(e: Exception){
                 withContext(Dispatchers.Main){_uiState.update { it.copy(error = e.message, isLoading = false) }}
             }
        }
    }

    private fun retrievedata(){
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val currentPrice=authRepo.getLocalUser()?.sessionPrice ?: 0

                withContext(Dispatchers.Main){_uiState.update { it.copy(sessionPrice = currentPrice, isLoading = false) }}
            }catch(e: Exception){
                withContext(Dispatchers.Main){_uiState.update { it.copy(isLoading = false, sessionPrice = 0) }}
            }
        }
    }

    init{
        retrievedata()
    }


}