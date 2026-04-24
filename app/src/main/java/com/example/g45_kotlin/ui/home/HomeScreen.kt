package com.uniandes.tutorias_g45k.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.uniandes.tutorias_g45k.R
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.user.TutorSummaryDto
import com.uniandes.tutorias_g45k.ui.home.components.SessionBanner
import com.uniandes.tutorias_g45k.utilities.AnalyticsManager
import com.uniandes.tutorias_g45k.utilities.GoogleAnalyticsService

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel(), onTutorClick: (TutorSummaryDto) -> Unit = {}, onMoreClick: () -> Unit = {}, onSessionClick: (String) -> Unit = {}) {
    val state by viewModel.state.collectAsState()
    val currentTime by viewModel.currentTime.collectAsStateWithLifecycle()
    val currentUserId = AuthHolder.authRepo.getCurrentUser()?.uid

    val sessionState = rememberLazyListState()

    val snappingLayout = remember(state) { SnapLayoutInfoProvider(sessionState) }

    val flingBehavior = rememberSnapFlingBehavior(snappingLayout)

    LaunchedEffect(Unit) {
        AnalyticsManager.setCurrentService("HOME_SERVICE")
        GoogleAnalyticsService.logScreenAccess("HomeScreen")
    }

    Scaffold(
        bottomBar = {

        }
    ) { padding ->
        PullToRefreshBox(isRefreshing = state.isLoading,
            onRefresh = viewModel::loadHomeData) {
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
                    Text("Próximas Sesiones", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.headlineLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (state.nextSessions.isEmpty() || state.areSessionLoading) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                if (state.areSessionLoading) {
                                    CircularProgressIndicator()
                                }else{
                                    val message=when (state.sessionError) {
                                        null -> "No hay sesiones programadas"
                                        else -> state.sessionError
                                    }
                                    Text(text=message!!, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    } else {
                        LazyRow(modifier=Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            state = sessionState,
                            flingBehavior = flingBehavior
                        ){
                            items(state.nextSessions) { session ->
                                SessionBanner(modifier=Modifier.fillParentMaxWidth(), session = session, onClick = { onSessionClick(it) }, currentUserId = currentUserId, currentTime=currentTime)
                            }
                        }
                    }
                }

                // Botones de Acción
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {onMoreClick()},
                            modifier = Modifier.weight(1f).height(100.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Add, null)
                                Text("Agendar Tutoría", textAlign = androidx.compose.ui.text.style.TextAlign.Center, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                        Button(
                            onClick = {},
                            modifier = Modifier.weight(1f).height(100.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Star, null)
                                Text("Ser Tutor", textAlign = androidx.compose.ui.text.style.TextAlign.Center, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }

                // Tutores Recomendados
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Tutores Recomendados", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.headlineLarge)
                        TextButton(onClick = {onMoreClick()}) { Text("Ver todos", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary) }
                    }
                }
                item {PullToRefreshBox(
                    isRefreshing = state.isLoading,
                    onRefresh = viewModel::loadHomeData
                ) {
                    LazyColumn(modifier=Modifier.heightIn(min=100.dp, max=200.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)){
                        if (state.isLoading) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                }
                            }
                        }else if (!state.featuredTutors.isEmpty()){
                            items(state.featuredTutors) { tutor ->
                                TutorItem(tutor, onSelection = { onTutorClick(tutor) })
                            }
                        }else{
                            item{
                                Column(){
                                    Text(
                                        modifier=Modifier.fillMaxWidth(),
                                        text= "Error cargando tutores destacados, arrastra para reintentar",
                                        color=MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                    }
                }
                }
            }
        }

    }
}

@Composable
fun TutorItem(tutor: TutorSummaryDto, onSelection: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick=onSelection),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = tutor.profileImageUrl,
                contentDescription = "Foto de ${tutor.name}",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(25.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.profile_placeholder),
                error = painterResource(R.drawable.profile_placeholder)
            )
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

