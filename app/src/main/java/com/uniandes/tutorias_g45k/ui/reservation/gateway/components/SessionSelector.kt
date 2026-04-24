package com.uniandes.tutorias_g45k.ui.reservation.gateway.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.uniandes.tutorias_g45k.ui.reservation.gateway.ReservationGatewayState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun SessionSelection(modifier: Modifier = Modifier,
                     state: ReservationGatewayState,
                     days:List<LocalDate>,
                     hours:List<String>,
                     onDateSelection:(LocalDate) -> Unit,
                     onHourSelection:(String) -> Unit){
    /*TODO*/



    Column(){
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            days.forEach { day ->
                DaySelector(
                    date = day,
                    selected = day == state.selectedDate,
                    onClick = {onDateSelection(day)}
                )
            }
        }
        Spacer(modifier = modifier.height(16.dp))
        HoursSelector(modifier = modifier.heightIn(max =100.dp), hours = hours, selectedDate=state.selectedDate, selectedHour = state.selectedHour, onHourSelection=onHourSelection, workingHours = hours)
    }
}

@Composable
fun DaySelector(modifier: Modifier = Modifier,
                date: LocalDate,
                selected:Boolean,
                onClick:() -> Unit){
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val enabled = date >= LocalDate.now().plusDays(1)
    val borderWidth = if (selected) 4.dp else 0.dp

    Surface(modifier=modifier
        .requiredHeight(100.dp)
        .widthIn(min = 50.dp)
        .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (enabled) backgroundColor else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(width = borderWidth, color = MaterialTheme.colorScheme.onPrimary),
        tonalElevation = 4.dp,
        shadowElevation = 5.dp){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es", "CO")).uppercase(),
                style=MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = modifier.height(8.dp))
            Text(
                text = date.dayOfMonth.toString(),
                style=MaterialTheme.typography.headlineSmall,
            )
        }
    }
}

@Composable()
fun HoursSelector(modifier: Modifier = Modifier, hours:List<String>, selectedDate: LocalDate, selectedHour:String, onHourSelection:(String) -> Unit, workingHours:List<String>) {
    val horas:List<String> =workingHours
    if (horas.isEmpty()){
        Surface(Modifier.heightIn(min = 50.dp, max = 120.dp).fillMaxWidth()){
            Text(
                text="No hay horas disponibles",
                style=MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color=MaterialTheme.colorScheme.error
            )
        }
    }else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            modifier = Modifier.heightIn(min = 50.dp, max = 120.dp)
        ) {
            items(horas.size) {
                val enabled =
                    LocalDateTime.now() < LocalDateTime.of(selectedDate, LocalTime.parse(horas[it]))
                HourOption(
                    hour = horas[it],
                    selected = horas[it] == selectedHour,
                    enabled = enabled,
                    onClick = onHourSelection,
                    modifier = modifier
                )
            }

        }
    }
}

@Composable()
fun HourOption (modifier: Modifier = Modifier,
                hour:String,
                selected:Boolean,
                enabled:Boolean,
                onClick:(String) -> Unit){
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val borderWidth = if (selected) 4.dp else 0.dp
    Surface(modifier=modifier
        .padding(5.dp)
        .fillMaxWidth()
        .requiredHeight(40.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(width = borderWidth, color = MaterialTheme.colorScheme.onPrimary),

        color = if (enabled) backgroundColor else MaterialTheme.colorScheme.surfaceVariant,
    ){
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
            Text(text=hour,
                modifier=modifier.clickable { onClick(hour) },
                style=MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}