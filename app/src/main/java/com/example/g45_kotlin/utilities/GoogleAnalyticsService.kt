package com.example.g45_kotlin.utilities

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.time.Instant


object GoogleAnalyticsService {
    private val analytics = Firebase.analytics
    private val httpClient = OkHttpClient()
    private const val ANALYTICS_URL = "http://10.0.2.2:8001/analytics/event"

    fun logScreenAccess(screenName: String) {
        Log.d("Analytics", "Logging screen access: $screenName")
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
    }

    fun logButtonClick(buttonName: String, screenName: String = "unknown", buttonAction: String = "unknown") {
        Log.d("Analytics", "Logging button click: $buttonName, in screen $screenName")
        analytics.logEvent("button_click") {
            param(FirebaseAnalytics.Param.ITEM_NAME, buttonName)
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
    }

    fun logEvent(eventType: String, metadata: Map<String, Any>, userId: String = "kotlin_user") {
        analytics.logEvent(eventType) {
            metadata.forEach { (k, v) -> param(k, v.toString()) }
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val metaJson = JSONObject(metadata.mapValues { it.value.toString() })
                val body = JSONObject().apply {
                    put("user_id", userId)
                    put("event_type", eventType)
                    put("metadata", metaJson)
                    put("timestamp", Instant.now().toString())
                }
                val request = Request.Builder()
                    .url(ANALYTICS_URL)
                    .post(body.toString().toRequestBody("application/json".toMediaType()))
                    .build()
                httpClient.newCall(request).execute().close()
            } catch (e: Exception) {
                Log.d("Analytics", "G45-Analytics post failed: ${e.message}")
            }
        }
    }
}
