package com.uniandes.tutorias_g45k.ui.provisional_profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.tutorias_g45k.ui.LoadingDialog
import com.uniandes.tutorias_g45k.ui.theme.AppTheme
import com.uniandes.tutorias_g45k.utilities.GoogleAnalyticsService

@Composable
fun ProvisionalScreen(modifier: Modifier = Modifier, viewModel: ProfileViewModel = viewModel(), onReservationClick:(String)->Unit){
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        GoogleAnalyticsService.logScreenAccess("ProfileScreen")
    }
    LoadingDialog(
        show = uiState.isLoading,
        onDismissRequest = {}
    )
    if (uiState.sessions.isNotEmpty()){
        LazyColumn(modifier=modifier.heightIn(min=300.dp, max=600.dp)
            .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ){
            uiState.sessions.forEach { session->
                item {
                    ReservationCard(modifier=modifier.fillMaxWidth(), id=session.id ?: "", status=session.status, date=session.scheduledAt, onClick = onReservationClick)
                }
            }
        }
    }else{
        Column(modifier=modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
            ){
            Text(
                modifier=modifier.fillMaxWidth(),
                text=if (uiState.error!="" || !uiState.connected) uiState.error else "No se encontraron reservas",
                color=MaterialTheme.colorScheme.error
            )
            Button(onClick = {viewModel.retriveSessions()}) {
                Text("Reintentar")
            }
        }

    }
}

@Composable
fun ReservationCard(modifier:Modifier=Modifier, id:String, status:String, date:String, onClick:(String)->Unit){
    Surface(modifier=modifier.clickable { onClick(id) }.heightIn(min=100.dp, max=200.dp),
        color = MaterialTheme.colorScheme.tertiary,
        shape= RoundedCornerShape(50.dp)
    ){
        Column(modifier=modifier.fillMaxWidth().padding( vertical=20.dp, horizontal=35.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start
        ){
            Text(text="Reserva ${id}", style=MaterialTheme.typography.headlineMedium)
            Text(text=status, style=MaterialTheme.typography.titleMedium)
            Text(text="Agenda para: ${date}", style=MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview
@Composable
fun ReservationCardPreview(){
    AppTheme(){
        ReservationCard(modifier = Modifier.fillMaxWidth(), id="123", status="Pendiente", date="sadasd", onClick = {})
    }
}
