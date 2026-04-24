package com.uniandes.tutorias_g45k.ui.reservation.summary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uniandes.tutorias_g45k.R
import com.uniandes.tutorias_g45k.ui.theme.AppTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DetailsBanner(
    modifier: Modifier = Modifier,
    date:LocalDateTime,
    skill:String
    ) {
    ElevatedCard(
        modifier = modifier.clip(RoundedCornerShape(50.dp)), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(modifier=Modifier.padding(horizontal = 20.dp, vertical=10.dp).fillMaxWidth()){
            Row(modifier=Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){
                Surface(
                    modifier = Modifier.sizeIn(minWidth = 50.dp, minHeight = 75.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(50.dp)

                ) {
                    Column(Modifier.padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                        //Month
                        Text(
                            text = date.month.getDisplayName(TextStyle.SHORT, Locale("es", "CO")).uppercase(),
                            textAlign = TextAlign.Center,
                            modifier = Modifier,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        //Day
                        Text(
                            text = date.dayOfMonth.toString(),
                            modifier = Modifier,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Text(text = skill,
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 25.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically){
                Icon(
                    painter= painterResource(id=R.drawable.session_schedule),
                    contentDescription = "time",
                    modifier = Modifier.requiredSize(35.dp),
                    tint = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(Modifier.padding(5.dp))
                val endHour=date.plusHours(1)
                val startTime=date.format(DateTimeFormatter.ofPattern("hh:mm a"))
                val endTime=endHour.format(DateTimeFormatter.ofPattern("hh:mm a"))

                Text(
                    text = "$startTime - $endTime",
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}

@Preview
@Composable
fun DetailsBannerPreview(){
    AppTheme(darkTheme = true) {
        Surface(){
            DetailsBanner(Modifier.fillMaxWidth(), LocalDateTime.now(), "Calculo Diferencial")
        }
    }
}