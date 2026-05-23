package com.uniandes.tutorias_g45k.ui.profile.pages.pqrs

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uniandes.tutorias_g45k.data.local.PqrDraftManager

class PqrViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PqrViewModel::class.java)) {
            val draftManager = PqrDraftManager.getInstance(context)
            @Suppress("UNCHECKED_CAST")
            return PqrViewModel(draftManager = draftManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
