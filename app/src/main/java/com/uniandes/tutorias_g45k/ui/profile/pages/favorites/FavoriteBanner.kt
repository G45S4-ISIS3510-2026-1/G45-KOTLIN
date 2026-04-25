package com.uniandes.tutorias_g45k.ui.profile.pages.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.uniandes.tutorias_g45k.R
import com.uniandes.tutorias_g45k.data.recommendation.TutorSummaryDto
import com.uniandes.tutorias_g45k.ui.theme.AppTheme

@Composable
fun FavoriteBanner(tutor: TutorSummaryDto, onSelection: (String) -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick={onSelection(tutor.id)}),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ParticipantIcon(tutor.profileImageUrl ?: "")
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tutor.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text(tutor.major, style=MaterialTheme.typography.labelMedium)
            }
            Column(){
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                    Text(tutor.rating.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PriceChange, null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                    Text("${(tutor.sessionPrice/1000)}k/h", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
        placeholder=painterResource(R.drawable.profile_placeholder),
        error=painterResource(R.drawable.profile_placeholder),
        fallback=painterResource(R.drawable.profile_placeholder)
    )
}


@Preview
@Composable
fun SessionBannerPreview(){
    AppTheme(darkTheme = true){
        FavoriteBanner(tutor = TutorSummaryDto(
            id = "jij",
            name = "mario",
            major = "mario",
            rating = 5.0,
            sessionPrice = 10000,
        ))
    }
}
