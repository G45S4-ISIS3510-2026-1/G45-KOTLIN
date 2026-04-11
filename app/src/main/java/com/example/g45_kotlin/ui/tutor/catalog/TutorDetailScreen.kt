package com.example.g45_kotlin.ui.tutor.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.g45_kotlin.data.catalog.ReviewResponse
import com.example.g45_kotlin.data.catalog.TutorResponse
import com.example.g45_kotlin.ui.theme.AppTheme
import com.example.g45_kotlin.utilities.GoogleAnalyticsService

@Composable
fun TutorDetailScreen(tutor: TutorResponse, reseñas: List<ReviewResponse>, skills: List<String> = emptyList(), onBack: () -> Unit, onBook: () -> Unit ={}) {
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit){
        GoogleAnalyticsService.logScreenAccess("TutorDetail")
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Top Section: Image and Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, colorScheme.surfaceVariant),
                                    startY = 500f
                                )
                            )
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.background(colorScheme.background.copy(alpha = 0.7f), CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = colorScheme.onBackground)
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorito", tint = Color.Red)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 24.dp, bottom = 24.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(colorScheme.surface)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.background)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(
                                text = tutor.name,
                                style = MaterialTheme.typography.headlineMedium,
                                color = colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = tutor.major,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // 2. Stats Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("PUNTAJE", "${tutor.tutorRating} ★", colorScheme.onBackground)
                    VerticalDivider(colorScheme.outlineVariant)
                    StatItem("TUTORÍAS", 5.toString(), colorScheme.onBackground)
                    VerticalDivider(colorScheme.outlineVariant)
                    StatItem("NIVEL", "Pregrado", colorScheme.onBackground)
                }

                // 3. Especialidades
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = "Especialidades",
                        style = MaterialTheme.typography.headlineSmall,
                        color = colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        skills.forEach { tag ->
                            Surface(
                                color = colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, colorScheme.outline)
                            ) {
                                Text(
                                    text = tag,
                                    color = colorScheme.primary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 4. Contact Info Hidden Card
                Card(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = colorScheme.outline, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "********@uniandes.edu.co", color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = colorScheme.onPrimary)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Información de contacto oculta",
                            color = colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Reserva una sesión para entrar en contacto con el tutor",
                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 5. Reseñas
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = "Reseñas",
                        style = MaterialTheme.typography.headlineSmall,
                        color = colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    reseñas.forEach { reseña ->
                        ReviewCard(reseña, colorScheme)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
            ) {
                Button(
                    onClick = {onBook()},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "Programar Sesión",
                        color = colorScheme.onPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, textColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = textColor.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun VerticalDivider(color: Color) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .width(1.dp)
            .background(color)
    )
}

@Composable
fun ReviewCard(reseña: ReviewResponse, colorScheme: ColorScheme) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colorScheme.outlineVariant)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = reseña.authorName, color = colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    Text(text = reseña.createdAt, color = colorScheme.onSurfaceVariant, fontSize = 10.sp)
                }
                Row {
                    repeat(5) { index ->
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < reseña.rating) Color(0xFFFFD700) else colorScheme.outlineVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = reseña.details,
                color = colorScheme.onSurface,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailPreview() {
    AppTheme {
        TutorDetailScreen(
            tutor = TutorResponse(
                id = "1",
                email = "ksdajidas",
                name = "Manolo",
                profileImageUrl = "toffoe"
            ),
            reseñas=emptyList(),
            onBack = {}
        )
    }
}
