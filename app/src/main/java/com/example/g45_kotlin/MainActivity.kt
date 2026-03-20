package com.example.g45_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.g45_kotlin.data.auth.AuthHolder
import com.example.g45_kotlin.data.auth.AuthRepository
import com.example.g45_kotlin.ui.theme.AppTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        AuthHolder.authRepo= AuthRepository.getInstance(this)
        setContent {
            AppTheme {
                val navController = rememberNavController()
                MainScreen(navController = navController)
            }
        }
    }
}






