package com.uniandes.tutorias_g45k.ui.agenda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uniandes.tutorias_g45k.data.reservation.AvailabilityDto
import com.uniandes.tutorias_g45k.ui.NoContentOrConnectionWidget
import com.uniandes.tutorias_g45k.ui.agenda.components.AvailabilitySlot
import com.uniandes.tutorias_g45k.ui.agenda.components.WeekDaySelector
import com.uniandes.tutorias_g45k.ui.theme.AppTheme
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor


@Composable
fun AvailabilityScreen(modifier: Modifier=Modifier,
                       onSelectWeekDay: (Int) -> Unit = {},
                       selectedHours: List<Int> = emptyList(),
                       selectedWeekDay: Int = 0,
                       isTutor: Boolean = false
                       ){
    val hours=(7..20).toList()
    val hourStrings=hours.map{"2026-05-16T${if (it<10) "0" else ""}${it}:00:00-05:00"}
    val connected by NetworkMonitor.isOnline.collectAsState()
    Column(modifier=modifier, verticalArrangement = Arrangement.SpaceEvenly){
        if (!connected){
            Column(){
                Text(
                    text="Sin conexión",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text="Actualmente no tiene conexión, por lo que la disponibilidad mostrada podría estar desactualizadas. Por favor revice su conexión y refresque",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (!isTutor){
            Box(modifier=Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                NoContentOrConnectionWidget(size=100, message="No eres tutor activo. Vuelvete tutor y define tu horario de disponibilidad.", text_style = MaterialTheme.typography.headlineSmall, missingConnection = !connected)
            }
        }else{
            WeekDaySelector(modifier=Modifier.fillMaxWidth(), onDaySelected = onSelectWeekDay, selectedDay = selectedWeekDay)
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)){
                items(hourStrings.size){
                        index->
                    AvailabilitySlot(slot = hourStrings[index], present = selectedHours.contains(hours[index]))
                }
            }
        }
    }

}

@Composable
@Preview
fun AvailabilityScreenPreview(){
    AppTheme(useSensor = false, darkTheme = true) {
        Surface(){
            AvailabilityScreen()
        }
    }
}