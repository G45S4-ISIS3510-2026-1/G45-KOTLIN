package com.uniandes.tutorias_g45k.ui.reservation.summary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.uniandes.tutorias_g45k.R
import com.uniandes.tutorias_g45k.ui.theme.AppTheme

const val urlPlacer = "https://media.licdn.com/dms/image/v2/D4E03AQErWMZGSWwcqg/profile-displayphoto-scale_200_200/B4EZxJq0pSLAAY-/0/1770762491069?e=1773878400&v=beta&t=UABxv2St6FPX2OI3Tu_9EdEg5GkC_1PryOLmQLovLRA"

@Composable
fun Participants (modifier: Modifier = Modifier,
                  tutorName:String,
                  tutorPicture:String,
                  studentName:String,
                  studentPicture:String
                  ){
    Column(modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        ParticipantCard(modifier=Modifier.fillMaxWidth().padding(vertical = 10.dp), url=tutorPicture, name=tutorName, isTutor = true)
        ParticipantCard(modifier=Modifier.fillMaxWidth().padding(vertical = 10.dp), url=studentPicture, name=studentName, isTutor = false)
    }

}

@Composable
fun ParticipantIcon(modifier: Modifier = Modifier, url:String=urlPlacer){
    AsyncImage(
        model = url,
        contentDescription = "UserProfilePic",
        modifier = Modifier
            .requiredSize(75.dp)
            .clip(RoundedCornerShape(50.dp)),
        contentScale = ContentScale.Crop,
        placeholder=painterResource(R.drawable.profile_placeholder),
        error=painterResource(R.drawable.profile_placeholder),
        fallback=painterResource(R.drawable.profile_placeholder)
    )
}

@Composable
fun ParticipantCard (modifier: Modifier = Modifier, url:String=urlPlacer, name:String="Nombre", isTutor:Boolean=true){
    ElevatedCard(
        modifier = modifier.clip(RoundedCornerShape(50.dp)), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ){
        Row(modifier=Modifier.padding(10.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start){
            ParticipantIcon(modifier=Modifier.fillMaxWidth(fraction = 0.40f), url=url)
            Column(modifier=Modifier.fillMaxWidth().padding(start = 10.dp),
                horizontalAlignment = Alignment.Start){
                Text(text=name,
                    modifier=Modifier.fillMaxWidth(),
                    style=MaterialTheme.typography.headlineSmall)
                if (isTutor){
                    Text(text="Tutor(a)",
                        modifier=Modifier.fillMaxWidth(),
                        style=MaterialTheme.typography.titleLarge,
                        color=MaterialTheme.colorScheme.tertiary)
                }else{
                    Text(text="Estudiante",
                        modifier=Modifier.fillMaxWidth(),
                        style=MaterialTheme.typography.titleLarge,
                        color=MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
}

@Preview
@Composable
fun ParticipantCardPreview(){
    AppTheme(darkTheme = true){
        Surface(){
            Participants(Modifier.fillMaxWidth(), "Juan Perez", "https://media.licdn.com/dms/image/v2/D4E03AQ", "Anita", "https://media.licdn.com/dms/image/v2/D4E03AQ")
        }
    }
}
