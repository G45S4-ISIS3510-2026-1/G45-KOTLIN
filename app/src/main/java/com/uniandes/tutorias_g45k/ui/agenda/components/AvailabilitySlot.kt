package com.uniandes.tutorias_g45k.ui.agenda.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uniandes.tutorias_g45k.ui.theme.AppTheme

import java.time.OffsetDateTime



fun getHourFromOffsetDateTime(isoString: String, offset: Int = 0):String{
    val odt = OffsetDateTime.parse(isoString)
    val localHour = odt.hour+offset
    val minute = odt.minute
    return formarHourString(localHour, minute)
}

fun formarHourString(hour:Int, minute:Int):String{
    val nonMilitarHour=if (hour<=12) hour else hour-12
    val moment= if (hour>=12) "PM" else "AM"
    return "${if (nonMilitarHour<10) "0" else ""}$nonMilitarHour:${if (minute < 10) "0" else ""}$minute $moment"
}

@Composable
fun AvailabilitySlot(modifier: Modifier = Modifier, slot:String= "2026-05-16T11:30:00-05:00", present:Boolean=true){
    val color=if (present) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant
    Card(modifier = modifier.fillMaxWidth().heightIn(min=50.dp, max=100.dp),
        colors= CardDefaults.cardColors(color),
        border = BorderStroke(3.dp, MaterialTheme.colorScheme.surfaceVariant),
        ) {
        Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.Center){
                Text(text = getHourFromOffsetDateTime(slot),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(text = "|", style = MaterialTheme.typography.bodyLarge)
                Text(text = getHourFromOffsetDateTime(slot, 1),
                    style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier=Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Text(
                    text = if (present) "Seleccionada" else "Libre",
                    style = MaterialTheme.typography.headlineLarge
                )
            }

        }
    }
}

@Composable
@Preview(showBackground = true)
fun AvailabilitySlotPreview(){
    AppTheme(useSensor = false, darkTheme = false){
            AvailabilitySlot()
    }
}