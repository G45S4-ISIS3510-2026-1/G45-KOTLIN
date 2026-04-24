package com.uniandes.tutorias_g45k.ui.profile.pages.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.tutorias_g45k.ui.NoContentOrConnectionWidget
import com.uniandes.tutorias_g45k.utilities.GoogleAnalyticsService
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor

@Composable
fun ReservationListScreen(modifier: Modifier = Modifier, viewModel: FavoriteListViewModel = viewModel(), onReservationClick:(String)->Unit, onBack:()->Unit={}){
    val uiState by viewModel.uiState.collectAsState()
    val connected by NetworkMonitor.isOnline.collectAsState()



    LaunchedEffect(Unit) {
        GoogleAnalyticsService.logScreenAccess("FavoritesList")
    }


    Column(modifier=modifier.fillMaxHeight().padding(20.dp)){
        Spacer(modifier=Modifier.height(30.dp))
        Row(modifier=Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Button(onClick = {onBack()}, modifier=modifier.requiredSize(50.dp), shape= CircleShape) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "GoBack",
                    modifier = Modifier
                        .requiredSize(25.dp)
                        .fillMaxSize()
                )
            }
            Spacer(modifier=Modifier.width(10.dp))
            Text(text="Favoritos",
                modifier=Modifier.fillMaxWidth(fraction = 0.75f),
                style=MaterialTheme.typography.displayMedium
            )
        }
        Spacer(modifier=Modifier.height(20.dp))
        if(!connected ){
            Column(){
                Text(
                    text="Sin conexión",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier=Modifier.height(20.dp))
        }

        PullToRefreshBox(isRefreshing = uiState.isLoading, onRefresh = {}, modifier=Modifier.weight(1f).fillMaxWidth()) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)){
                if (uiState.error!="" && uiState.favorites.isEmpty() && !uiState.isLoading){
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        NoContentOrConnectionWidget(
                            modifier = Modifier.sizeIn(150.dp, 200.dp),
                            size = 100,
                            message = uiState.error,
                            text_style = MaterialTheme.typography.headlineSmall,
                            missingConnection = !connected
                        )
                    }
                }else{
                    if (uiState.favorites.isEmpty() && !uiState.isLoading){
                        item{
                            Spacer(modifier=Modifier.height(20.dp))
                            NoContentOrConnectionWidget(modifier = Modifier.sizeIn(150.dp, 200.dp), size = 100, message = "No se encontraron reservas/sesiones", text_style = MaterialTheme.typography.headlineSmall, missingConnection = !connected)
                        }
                    }else{
                       // items(uiState.favorites.size){index->
                       //     FavoriteBanner(modifier = Modifier.fillMaxWidth(), session = uiState.sessions[index], onClick = onReservationClick, currentUserId = AuthHolder.authRepo.getCurrentUser()?.uid)
                       // }
                    }
                }

            }
        }

    }
}

@Composable
fun FilterButton(modifier: Modifier = Modifier, label:String, onClick: () -> Unit, selected:Boolean){
    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = color),) {
        Text(text=label,
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
        )
    }
}

