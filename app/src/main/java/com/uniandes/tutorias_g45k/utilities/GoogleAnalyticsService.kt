package com.uniandes.tutorias_g45k.utilities

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent


object GoogleAnalyticsService {

    fun setUserId(userId: String) {
        AnalyticsManager.setUserId(userId)
    }

    fun logScreenAccess(screenName: String, screenClass: String = screenName) {
        AnalyticsManager.logScreenView(screenName, screenClass)
    }

    fun logButtonClick(buttonName: String, screenName: String) {
        AnalyticsManager.logServiceInteraction(
            eventName = "button_click",
            serviceName = "UI_INTERACTION",
            referenceId = "btn_$buttonName",
            screenName = screenName,
            itemName = buttonName
        )
    }
}

