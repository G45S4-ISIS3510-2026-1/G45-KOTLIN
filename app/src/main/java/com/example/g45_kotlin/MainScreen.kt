package com.example.g45_kotlin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.g45_kotlin.ui.LoadingDialog
import com.example.g45_kotlin.ui.theme.AppTheme

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Inicio", Icons.Default.Home),
    CATALOG("Catalogo", Icons.Default.Search),
    AGENDA("Agenda", Icons.Default.DateRange),
    MESSAGES("Mensajes", Icons.Default.Email),
    PROFILE("Perfil", Icons.Default.AccountCircle),
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, navController: NavHostController= rememberNavController()){
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    //Verify logged in with firebase auth instance
    val mostrarBottomBar = currentDestination?.route in setOf(AppDestinations.HOME.label, AppDestinations.CATALOG.label, AppDestinations.AGENDA.label, AppDestinations.MESSAGES.label, AppDestinations.PROFILE.label)
    Scaffold(modifier=modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(visible = mostrarBottomBar ){
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ){
                    AppDestinations.entries.forEach{
                        NavigationBarItem(
                            selected = currentDestination?.route == it.label,
                            onClick = { navController.navigate(it.label) },
                            icon = {Icon(imageVector = it.icon, contentDescription = it.label)},
                            label = {Text(text = it.label)}
                        )
                    }
                }
            }
        }
    ){paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.home,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.home) { Text(text="Inicio")}
            composable(Routes.catalog) { Text(text="Catalogo")}
            composable(Routes.agenda) { PendingPage(modifier=Modifier.fillMaxSize())}
            composable(Routes.messages) { Text(text="Mensajes")}
            composable(Routes.profile) { Text(text="Perfil")}
            composable(Routes.tutorDetail) { Text(text="Tutor Detail")}
            composable(Routes.reservationSummary+"/{session_id}") { Text(text="Reservation Summary")}
            composable(Routes.reservationGateway+"/{tutor_id}") {Text(text="Reservation Summary")}
        }
    }
}

@Composable
fun PendingPage(modifier: Modifier=Modifier){
    Card(
        modifier = modifier
            .fillMaxWidth(0.8f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "In Progress...",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview(){
    AppTheme(darkTheme = false){
        MainScreen()
    }
}

