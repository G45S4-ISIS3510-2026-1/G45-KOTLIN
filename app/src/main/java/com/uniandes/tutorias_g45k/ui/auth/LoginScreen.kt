package com.uniandes.tutorias_g45k.ui.auth

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.tutorias_g45k.R
import com.uniandes.tutorias_g45k.utilities.AnalyticsManager
import com.uniandes.tutorias_g45k.utilities.GoogleAnalyticsService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {},
    onFail: () -> Unit = {}
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val connected by NetworkMonitor.isOnline.collectAsState()

    // Reportamos a Analytics que estamos en el servicio de Autenticación
    LaunchedEffect(Unit) {
        AnalyticsManager.setCurrentService("AUTH_SERVICE")
        GoogleAnalyticsService.logScreenAccess("LoginScreen")
    }

    fun signInWithMicrosoft() {
        viewModel.onLoginStarted()
        val provider = OAuthProvider.newBuilder("microsoft.com")
        provider.addCustomParameter("prompt", "select_account")
        
        val auth = FirebaseAuth.getInstance()
        auth.startActivityForSignInWithProvider(context as ComponentActivity, provider.build())
            .addOnFailureListener { e ->
                val message = e.message
                viewModel.onLoginError("Error al iniciar sesión,por favor reinicie la aplicación e intente denuevo" ?: "Error desconocido")
                // También logueamos el error de forma personalizada
                AnalyticsManager.logError("AUTH_SERVICE", "Login fallido: ${message}", e as? Exception)
                Toast.makeText(context, "Error al iniciar sesión. Borre datos de la aplicación e intente denuevo", Toast.LENGTH_LONG).show()
            }
            .addOnCompleteListener {task->
                if(task.isSuccessful){
                    val isNewUser=task.result?.additionalUserInfo?.isNewUser
                    if (isNewUser==true){
                        viewModel.onNewLogin(onFail)
                    }else{
                        viewModel.onPreviousLogin()
                    }
                }
            }.addOnSuccessListener {
                viewModel.onLoginSuccess(onLoginSuccess, onFail)
                Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show()
            }

    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            // Contenido del Login
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {

                    Surface(
                        modifier = Modifier.size(95.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.app_icon),
                                contentDescription = null,
                                modifier = Modifier.size(70.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Tutorías Uniandes",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    if (state.error != null) {
                        Text(
                            text = state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Button(
                        onClick = { signInWithMicrosoft() },
                        enabled = !state.isLoading && connected,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (connected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            contentColor = if (connected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(27.dp)
                    ) {
                        Text(text = "Iniciar Sesion con Microsoft", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

