package com.uniandes.tutorias_g45k.ui.tutor.become

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uniandes.tutorias_g45k.data.local.BecomeTutorDraftManager

class BecomeTutorViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BecomeTutorViewModel::class.java)) {
            val draftManager = BecomeTutorDraftManager.getInstance(context)
            @Suppress("UNCHECKED_CAST")
            return BecomeTutorViewModel(draftManager = draftManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
