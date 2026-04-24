package com.uniandes.tutorias_g45k.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.uniandes.tutorias_g45k.R
import com.uniandes.tutorias_g45k.ui.theme.AppTheme

@Composable
fun ProfileBanner(modifier: Modifier=Modifier, name:String="Andres Buitrago", major:String="Derecho", rating:Double?=null, code:String="202310273", imageUrl:String="https://media.licdn.com/dms/image/v2/D4E03AQErWMZGSWwcqg/profile-displayphoto-crop_800_800/B4EZxJq0pSLAAI-/0/1770762490962?e=1778716800&v=beta&t=khP8DFqey-djTQzSuecIN4GTT-JnPEjWwDdBxAPyrls", onStartPqr:()->Unit={}){
    Box(modifier=Modifier.background(Color.Transparent)){
        Row(modifier=Modifier.align(Alignment.TopEnd), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End){
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Help,
                contentDescription = "GoBack",
                modifier = Modifier
                    .requiredSize(30.dp)
                    .fillMaxSize()
                    .clickable { onStartPqr() },
                tint=MaterialTheme.colorScheme.onSurface
            )
            Text(text="PQR",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface)
        }

        Column(modifier=modifier.background(color=Color.Transparent), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
            AsyncImage(
                model = imageUrl,
                contentDescription = "ProfilePic",
                modifier = Modifier
                    .requiredSize(125.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder=painterResource(R.drawable.profile_placeholder)
            )
            Text(
                text = name,
                textAlign= TextAlign.Center,
                maxLines = 2,
                overflow= TextOverflow.Ellipsis,
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = major,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier=Modifier.height(10.dp))
            Row(modifier=Modifier.background(color=Color.Transparent), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center,){
                ProfileCard(Modifier.weight(1f), label = "Código", value = code)
                Spacer(modifier=Modifier.width(10.dp))
                if (rating!=null){
                    ProfileCard(modifier=Modifier.weight(1f), label = "Rating", value = rating.toString())
                }else{
                    ProfileCard(modifier=Modifier.weight(1f), label = "Rating", value = "N/A")
                }

            }
        }
    }
}

@Composable
fun ProfileCard(modifier: Modifier=Modifier, label:String, value:String){
    Card(modifier=modifier.widthIn(150.dp),
        shape=RoundedCornerShape(20.dp),
        colors= CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)){
        Column(modifier=Modifier.padding(vertical=10.dp, horizontal = 15.dp), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center){
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileBannerPreview(){
    AppTheme(darkTheme = true, useSensor = false){
        Surface(modifier=Modifier.padding(20.dp), color = MaterialTheme.colorScheme.surface){
            ProfileBanner(name="andres garcia",major="Ing. de sdasd", code="201232162", rating=3.1, imageUrl="casas" )
        }
    }

}