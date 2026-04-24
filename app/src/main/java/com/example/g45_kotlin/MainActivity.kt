package com.example.g45_kotlin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.g45_kotlin.data.auth.AuthHolder
import com.example.g45_kotlin.data.auth.AuthRepository
import com.example.g45_kotlin.data.local.SearchHistoryManager
import com.example.g45_kotlin.data.local.ThemePreferenceManager
import com.example.g45_kotlin.data.recommendation.RecommendationDatabase
import com.example.g45_kotlin.ui.theme.AppTheme
import com.example.g45_kotlin.utilities.LightSensorManager
import com.example.g45_kotlin.utilities.NetworkMonitor
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var lightSensorManager: LightSensorManager
    private lateinit var themeManager: ThemePreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize LightSensorManager
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
                MainScreen(navController = navController,  onChangePreference={changeDynamicTheme(!isDynamic)}, isDynamic = isDynamic)
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


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (Tiramisu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                }
                else -> {
                    // Directly ask for the permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

}






