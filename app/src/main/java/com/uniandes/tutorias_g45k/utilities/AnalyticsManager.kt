package com.uniandes.tutorias_g45k.utilities

import com.google.firebase.crashlytics.FirebaseCrashlytics

object AnalyticsManager {
    fun setCurrentService(serviceName: String) {
        // Esta línea es la que responde la pregunta de negocio.
        // Si la app falla, Crashlytics nos dirá en qué "service" estaba.
        FirebaseCrashlytics.getInstance().setCustomKey("current_service", serviceName)
    }

    fun logError(service: String, message: String, exception: Exception? = null) {
        FirebaseCrashlytics.getInstance().setCustomKey("error_source", service)
        if (exception != null) {
            FirebaseCrashlytics.getInstance().recordException(exception)
        } else {
            FirebaseCrashlytics.getInstance().log("Manual Error in $service: $message")
        }
    }
}
