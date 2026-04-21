package com.example.g45_kotlin.ui.profile.pages.reservations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.g45_kotlin.R
import com.example.g45_kotlin.data.reservation.ParticipantSummaryDto
import com.example.g45_kotlin.data.reservation.SessionDto
import com.example.g45_kotlin.data.reservation.SkillSummaryDto
import com.example.g45_kotlin.ui.reservation.summary.Status
import com.example.g45_kotlin.ui.theme.AppTheme
import java.time.ZonedDateTime
import kotlin.math.abs

@Composable
fun SessionBanner(modifier: Modifier = Modifier,
                  session: SessionDto,
                  onClick: (String) -> Unit = {},
                  currentUserId: String? = null,) {

    val sessionLocalDateTime= ZonedDateTime.parse(session.scheduledAt).toLocalDateTime()

    Card(
        modifier = modifier.requiredHeightIn(min = 150.dp, max = 200.dp).clickable(onClick = { onClick(session.id!!) }),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Surface(modifier = Modifier.padding(15.dp).fillMaxWidth(), color = MaterialTheme.colorScheme.surfaceVariant) {
            Column(modifier=Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start){
                    val otherParticipant=when(currentUserId){
                        session.student.id -> session.tutor
                        session.tutor.id -> session.student
                        else -> session.student
                    }
                    ParticipantIcon(imageUrl = otherParticipant.profileImageUrl ?: "")
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth(fraction = 0.6f)){
                        Text(
                            text = "Tutoria con: ",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = otherParticipant.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = session.skill.label,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(){
                        Text(
                            text = "${sessionLocalDateTime.dayOfMonth}/${sessionLocalDateTime.monthValue}/${sessionLocalDateTime.year}",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "${if (sessionLocalDateTime.hour>12) abs(sessionLocalDateTime.hour-12) else sessionLocalDateTime.hour}:${if (sessionLocalDateTime.minute<10) "0" else ""}${sessionLocalDateTime.minute}\n${if (sessionLocalDateTime.hour>12) "PM" else "AM"}",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
                val color = if (session.status==Status.CONCLUDED.status){MaterialTheme.colorScheme.tertiary}else if (session.status==Status.PENDING.status){MaterialTheme.colorScheme.primary} else {MaterialTheme.colorScheme.error}
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    color = color,
                    shape = RoundedCornerShape(50.dp)
                ){
                    Text(
                        modifier=Modifier.padding(5.dp),
                        textAlign = TextAlign.Center,
                        text = session.status,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

        }
    }
}

@Composable
fun ParticipantIcon(imageUrl: String){
    AsyncImage(
        model = imageUrl,
        contentDescription = "UserProfilePic",
        modifier = Modifier
            .requiredSize(75.dp)
            .clip(RoundedCornerShape(50.dp)),
        contentScale = ContentScale.Crop,
        placeholder=painterResource(R.drawable.profile_placeholder)
    )
}


@Preview
@Composable
fun SessionBannerPreview(){
    AppTheme(darkTheme = true){
        SessionBanner(session = SessionDto(
            student = ParticipantSummaryDto(id = "1", name = "Juan Perezuhh9huhuh", profileImageUrl = "https://media.licdn.com/dms/image/v2/D4E03AQ"), skill= SkillSummaryDto(id = "1", label = "Programaciónkknlknklnklnlnklnlnknlnknlknknl"), scheduledAt = "2026-04-14T16:00:05-05:00")
        )
    }
}
