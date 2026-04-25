package com.uniandes.tutorias_g45k.utilities

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Objeto Centralizador de Telemetría
 * Este objeto asegura que los parámetros enviados coincidan exactamente con
 * las Dimensiones Personalizadas configuradas en Firebase y Supabase.
 */
object AnalyticsManager {
    private val analytics: FirebaseAnalytics = Firebase.analytics
    private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    // Parámetros estandarizados según requerimiento de Auditoría
    private const val PARAM_SERVICE_NAME = "service_name"
    private const val PARAM_REFERENCE_ID = "reference_id"
    private const val PARAM_SCREEN_NAME = "screen_name"
    private const val PARAM_ITEM_NAME = "item_name" // Nuevo: Para "Nombre de Botón"

    /**
     * Registra una interacción con un servicio (Materia, Tutor, etc.)
     */
    fun logServiceInteraction(
        eventName: String, 
        serviceName: String, 
        referenceId: String, 
        screenName: String = "unknown",
        itemName: String? = null // Para el nombre del botón
    ) {
        val bundle = Bundle().apply {
            putString(PARAM_SERVICE_NAME, serviceName)
            putString(PARAM_REFERENCE_ID, referenceId)
            putString(PARAM_SCREEN_NAME, screenName)
            itemName?.let { putString(PARAM_ITEM_NAME, it) }
        }
        
        analytics.logEvent(eventName, bundle)
        
        // Contexto para Crashlytics
        crashlytics.setCustomKey("last_service", serviceName)
        crashlytics.setCustomKey("last_ref_id", referenceId)
    }

    /**
     * Registra un cambio de pantalla
     * Responde a: "¿En qué sección ocurren más errores?"
     */
    fun logScreenView(screenName: String, screenClass: String = "JetpackCompose") {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(PARAM_SCREEN_NAME, screenName) // Duplicamos para la dimensión personalizada
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
        
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        
        // Sincronizamos con Crashlytics
        crashlytics.setCustomKey("last_visible_screen", screenName)
    }

    fun logReservationAttempt(){
        val bundle=Bundle().apply {
            putString("description", "programar_reserva")
        }
        analytics.logEvent("reservation_intent", bundle)
    }

    fun logFilterSelection(selectedFilter: String, filterType: String){
        val bundle=Bundle().apply {
            putString("filter_type", filterType)
            putString("selected_option", selectedFilter)
        }
        analytics.logEvent("filter_selection", bundle)
    }

    fun logOrderSelection(orderType:String){
        val bundle=Bundle().apply {
            putString("order_type", orderType)
        }
        analytics.logEvent("order_selection", bundle)
    }

    fun logReviewSubmit(reviewLength:Int){
        val bundle=Bundle().apply {
            putInt("review_length", reviewLength)
        }
        analytics.logEvent("review_submit", bundle)
    }

    fun logPqrsSubmit(pqrsLength:Int, type:String){
        val bundle=Bundle().apply {
            putInt("pqrs_length", pqrsLength)
            putString("pqrs_type", type)
        }
        analytics.logEvent("pqrs_submit", bundle)
    }

    fun logScheduledSession(major:String, skill:String){
        val bundle=Bundle().apply {
            putString("major", major)
            putString("skill", skill)
        }
        analytics.logEvent("scheduled_session", bundle)
    }

    /**
     * Establece el servicio actual para contexto en reportes de error
     */
    fun setCurrentService(serviceName: String) {
        crashlytics.setCustomKey("last_service", serviceName)
    }

    /**
     * Establece el ID de usuario para trazabilidad entre dispositivos
     */
    fun setUserId(userId: String) {
        analytics.setUserId(userId)
        crashlytics.setUserId(userId)
    }

    /**
     * Log de errores con contexto enriquecido
     */
    fun logError(service: String, message: String, exception: Exception? = null) {
        crashlytics.setCustomKey("error_source", service)
        crashlytics.setCustomKey("error_details", message)
        
        if (exception != null) {
            crashlytics.recordException(exception)
        } else {
            crashlytics.log("E/$service: $message")
        }
    }
}

