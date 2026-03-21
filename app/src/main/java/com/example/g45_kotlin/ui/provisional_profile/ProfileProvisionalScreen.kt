package com.example.g45_kotlin.ui.provisional_profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.g45_kotlin.utilities.GoogleAnalyticsService

@Composable
fun ProvisionalScreen(modifier: Modifier = Modifier, viewModel: ProfileViewModel = viewModel(), onReservationClick:(String)->Unit){
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        GoogleAnalyticsService.logScreenAccess("ProfileScreen")
    }
    LazyColumn(modifier=modifier.heightIn(min=300.dp, max=600.dp)){
        uiState.sessions.forEach { session->
            item {
                ReservationCard(modifier=modifier.fillMaxWidth(), id=session.id ?: "", status=session.status, onClick = onReservationClick)
            }
        }
    }
}

@Composable
fun ReservationCard(modifier:Modifier=Modifier, id:String, status:String, onClick:(String)->Unit){
    Surface(modifier=modifier.clickable { onClick(id) }){
        Card(modifier.heightIn(min=100.dp, max=200.dp).fillMaxWidth()){
            Text(text=id, style=MaterialTheme.typography.titleLarge)
            Spacer(modifier=Modifier.height(10.dp))
            Text(text=status, style=MaterialTheme.typography.titleMedium)


        }
    }

}