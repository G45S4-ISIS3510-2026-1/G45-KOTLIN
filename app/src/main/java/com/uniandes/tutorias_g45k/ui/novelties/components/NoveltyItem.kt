package com.uniandes.tutorias_g45k.ui.novelties.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.uniandes.tutorias_g45k.R
import com.uniandes.tutorias_g45k.data.novelty.NoveltyDto
import com.uniandes.tutorias_g45k.data.novelty.NoveltyType
import com.google.firebase.Timestamp
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Composable
fun NoveltyItem(modifier: Modifier = Modifier, novelty: NoveltyDto, onClick: (NoveltyDto) -> Unit={}, onDiscard: (String) -> Unit = {}){
    var expanded by remember { mutableStateOf(false) }
    var icon=when(novelty.type){
        NoveltyType.SESSION.label-> R.drawable.session_reminder_icon
        NoveltyType.PRICE_CHANGE.label-> R.drawable.price_change_icon
        NoveltyType.INCOMING_SESION.label-> R.drawable.scheduled_session_icon
        NoveltyType.NEW_REVIEW.label-> R.drawable.received_review_icon
        else -> R.drawable.session_reminder_icon
    }
    val now= Timestamp.now().toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    val noveltyDate=novelty.createdAt.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    val daysDiff= ChronoUnit.DAYS.between(noveltyDate, now)
    val hoursDiff= ChronoUnit.HOURS.between(noveltyDate, now)-daysDiff*24
    val minutesDiff= ChronoUnit.MINUTES.between(noveltyDate, now)-hoursDiff*60

    val message=when{
        daysDiff>0L -> "$daysDiff d"
        hoursDiff>0L -> "$hoursDiff h"
        minutesDiff>0L -> "$minutesDiff min"
        else -> "Reciente"
    }

    if (novelty.type==NoveltyType.SESSION.label){
        if (novelty.title.contains("confirmada")){
            icon=R.drawable.confirmation
        }else if (novelty.title.contains("cancelada") || novelty.title.contains("vencida")){
            icon=R.drawable.cancel
        }
    }
    val expandIcon = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore
    Card(modifier = modifier.animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ){
       Surface(modifier = Modifier.padding(15.dp).fillMaxWidth(), color = MaterialTheme.colorScheme.surfaceVariant){
           Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start){
                Column(modifier = Modifier.fillMaxWidth(fraction = 0.2f).fillMaxHeight(), verticalArrangement = Arrangement.Top){
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = "Icono notificacion",
                        modifier = Modifier.requiredSize(30.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(modifier = Modifier.fillMaxWidth(fraction = 0.8f).fillMaxHeight(), verticalArrangement = Arrangement.Center){
                    Text(
                        modifier = Modifier.clickable(onClick={onClick(novelty)}),
                        text = novelty.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (expanded){
                        Text(
                            modifier = Modifier.clickable(onClick={onClick(novelty)}),
                            text = novelty.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                            Button(onClick = {
                                expanded=!expanded;
                                onDiscard(novelty.id)}){
                                Text("Descartar")
                            }
                        }

                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center){
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center){
                    Icon(
                        imageVector = expandIcon,
                        contentDescription = "Expand Icon",
                        modifier = Modifier.requiredSize(20.dp).clickable { expanded = !expanded },
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
           }
       }
    }
}