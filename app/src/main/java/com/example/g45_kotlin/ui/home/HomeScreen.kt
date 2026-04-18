package com.example.g45_kotlin.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.g45_kotlin.data.user.TutorSummaryDto
import com.example.g45_kotlin.utilities.AnalyticsManager
import com.example.g45_kotlin.utilities.GoogleAnalyticsService

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onBecomeTutor: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        AnalyticsManager.setCurrentService("HOME_SERVICE")
        GoogleAnalyticsService.logScreenAccess("HomeScreen")
    }

    Scaffold(
        bottomBar = {

        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Saludo
            item {
                Text(
                    text = "¡Hola, ${state.userName}!",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Próxima Sesión
            item {
                Text("Próxima Sesión", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))
                if (state.nextSession == null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No hay próxima sesión programada", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    // Aquí iría el diseño de la sesión
                }
            }

            // Botones de Acción
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f).height(100.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Add, null)
                            Text("Agendar Tutoría", textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 12.sp)
                        }
                    }
                    Button(
                        onClick = onBecomeTutor,
                        modifier = Modifier.weight(1f).height(100.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Star, null)
                            Text("Ser Tutor", textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Tutores Destacados
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Tutores Destacados", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    TextButton(onClick = {}) { Text("Ver todos") }
                }
            }

            if (state.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                if (!state.featuredTutors.isEmpty()) {
                    item {
                        LazyColumn(modifier=Modifier.heightIn(min=100.dp, max=200.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)){
                            items(state.featuredTutors) { tutor ->
                                TutorItem(tutor)
                            }
                        }
                    }
                }else{
                    item{
                        Column(){
                            Text(
                                modifier=Modifier.fillMaxWidth(),
                                text=state.error ?: "Error cargando tutores destacados",
                                color=MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Button(onClick=viewModel::loadHomeData){
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TutorItem(tutor: TutorSummaryDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(modifier = Modifier.size(50.dp), shape = RoundedCornerShape(25.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Box(contentAlignment = Alignment.Center) { Text(tutor.name.take(1)) }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tutor.name, fontWeight = FontWeight.Bold)
                Text(tutor.major, style=MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                Text(tutor.rating.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
