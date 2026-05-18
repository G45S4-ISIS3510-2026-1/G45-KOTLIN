package com.uniandes.tutorias_g45k

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.auth.AuthRepository
import com.uniandes.tutorias_g45k.data.local.SearchHistoryManager
import com.uniandes.tutorias_g45k.data.local.ThemePreferenceManager
import com.uniandes.tutorias_g45k.data.recommendation.RecommendationDatabase
import com.uniandes.tutorias_g45k.ui.theme.AppTheme
import com.uniandes.tutorias_g45k.utilities.LightSensorManager
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor
import com.google.firebase.FirebaseApp
import com.uniandes.tutorias_g45k.data.recommendation.TutorSummaryDto
import com.uniandes.tutorias_g45k.ui.tutor.catalog.TutorViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var lightSensorManager: LightSensorManager
    private lateinit var themeManager: ThemePreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize LightSensorManage
        lightSensorManager = LightSensorManager(applicationContext)
        themeManager = ThemePreferenceManager(applicationContext)
        enableEdgeToEdge()
        // Initialize Search History Preference
        SearchHistoryManager.getInstance(applicationContext)
        //Initialize ThemePreference Datastore

        // Initialize Network Monitor
        NetworkMonitor.startMonitoring(applicationContext)
        //Initialize Rooms
        RecommendationDatabase.getInstance(applicationContext)
        AuthHolder.authRepo= AuthRepository.getInstance(applicationContext)
        // Initialize Firebase
        FirebaseApp.initializeApp(applicationContext)
        fun changeDynamicTheme(active:Boolean){
            lifecycleScope.launch{
                themeManager.changeDynamicThemePreference(active)
            }
        }
        setContent {
            val lux by lightSensorManager.luxValue.collectAsState()
            val isDynamic by themeManager.isDynamicThemeActive.collectAsState(initial = false)
            AppTheme (luxValue = lux, useSensor = lightSensorManager.deviceHasLightSensor() && isDynamic) {
                val navController = rememberNavController()
                MainScreen(navController = navController,  onChangePreference={changeDynamicTheme(!isDynamic)}, isDynamic = isDynamic, intent = intent)
            }
        }
        askNotificationPermission()
    }
    override fun onStart(){
        super.onStart()
        lightSensorManager.start()
    }
    override fun onStop(){
        super.onStop()
        lightSensorManager.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkMonitor.stopMonitoring()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) //To detect new intents received due to the fcm service
    }



    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (Tiramisu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED -> {
                }
                else -> {
                    // Directly ask for the permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

}






