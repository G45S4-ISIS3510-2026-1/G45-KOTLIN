package com.example.g45_kotlin.utilities

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent


object GoogleAnalyticsService {
    private val analytics= Firebase.analytics

    fun logScreenAccess(screenName: String) {
        Log.d("Analytics", "Logging screen access: $screenName")
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenName) // En Compose, solemos usar el nombre de la ruta aquí también
        }
    }

    fun logButtonClick(buttonName: String, screenName: String = "unknown", buttonAction: String = "unknown") {
        Log.d("Analytics", "Logging button click: $buttonName, in screen $screenName")
        analytics.logEvent("button_click") {
            param(FirebaseAnalytics.Param.ITEM_NAME, buttonName)
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
    }
}