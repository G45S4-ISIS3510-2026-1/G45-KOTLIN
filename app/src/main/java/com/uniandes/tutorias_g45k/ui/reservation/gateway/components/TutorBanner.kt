package com.uniandes.tutorias_g45k.ui.reservation.gateway.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.uniandes.tutorias_g45k.R
import com.uniandes.tutorias_g45k.ui.reservation.gateway.TutorUser

@Composable
fun TutorBanner(modifier: Modifier = Modifier, tutorInfo: TutorUser){
    /*TODO*/
    //Implementar API/ViewModel//
    ElevatedCard(modifier=modifier.clip(RoundedCornerShape(50.dp)), colors= CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.secondary),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )){
        Row(modifier=modifier.padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start) {
            TutorIcon(modifier=modifier.fillMaxWidth(fraction = 0.40f), url=tutorInfo.picture)
            Column(modifier=modifier.fillMaxWidth(fraction = 0.60f).padding(5.dp)) {
                Text(text=tutorInfo.name,
                    modifier=modifier,
                    style=MaterialTheme.typography.headlineSmall
                )
                Text(text=tutorInfo.major,
                    modifier=modifier,
                    style=MaterialTheme.typography.bodyMedium
                )
                Text(text="${tutorInfo.currentRating} ★",
                    modifier=modifier,
                    style=MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun TutorIcon(modifier: Modifier = Modifier, url:String="https://media.licdn.com/dms/image/v2/D4E03AQErWMZGSWwcqg/profile-displayphoto-scale_200_200/B4EZxJq0pSLAAY-/0/1770762491069?e=1773878400&v=beta&t=UABxv2St6FPX2OI3Tu_9EdEg5GkC_1PryOLmQLovLRA"){
    AsyncImage(
        model = url,
        contentDescription = "TutorProfilePic",
        modifier = Modifier
            .requiredSize(90.dp)
            .clip(RoundedCornerShape(50.dp)),
        contentScale = ContentScale.Crop,
        placeholder=painterResource(R.drawable.profile_placeholder)
    )
}
