package com.uniandes.tutorias_g45k.ui.profile.pages.reviews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.tutorias_g45k.ui.NoContentOrConnectionWidget
import com.uniandes.tutorias_g45k.utilities.GoogleAnalyticsService
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor
import com.uniandes.tutorias_g45k.data.firestore.FirestoreReviewDto

@Composable
fun ReviewListScreen(
    modifier: Modifier = Modifier,
    viewModel: ReviewListViewModel = viewModel(),
    onReviewClick: (FirestoreReviewDto) -> Unit,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val dayFilter by viewModel.dayFilter.collectAsState()
    val isTutor by viewModel.isTutor.collectAsState()
    val connected by NetworkMonitor.isOnline.collectAsState()
    val ranges = listOf(7, 15, 30)

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        GoogleAnalyticsService.logScreenAccess("ReviewList")
    }

    LaunchedEffect(isTutor){
        listState.animateScrollToItem(0)
    }

    Column(modifier=modifier.fillMaxHeight().padding(20.dp)){
        Spacer(modifier=Modifier.height(30.dp))
        Row(modifier=Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            FilledIconButton(
                onClick = { onBack() },
                modifier = Modifier.requiredSize(50.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "GoBack",
                    tint = Color.Black,
                    modifier = Modifier
                        .requiredSize(25.dp)
                        .fillMaxSize()
                )
            }
            Spacer(modifier=Modifier.width(10.dp))
            Text(text="Reseñas",
                modifier=Modifier.fillMaxWidth(fraction = 0.75f),
                style=MaterialTheme.typography.displayMedium
            )
        }
        Spacer(modifier=Modifier.height(20.dp))
        if(!connected ){
            Column(){
                Text(
                    text="Modo Offline",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text="Actualmente no tiene conexión. Se muestran las reseñas almacenadas localmente. Conéctese a internet para obtener las más recientes.",
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier=Modifier.height(20.dp))
        }
        Text(
            text="Temporalidad",
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(modifier=Modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)){
            item{FilterButton(label = "Recientes", onClick = {viewModel.onSelectRange(1)}, selected = dayFilter==1)}
            items(ranges.size){
                FilterButton(label = "Hace ${ranges.elementAt(it)} dia(s)", onClick = {viewModel.onSelectRange(ranges.elementAt(it))}, selected = dayFilter==ranges.elementAt(it))
            }
            item{FilterButton(label = "Todas", onClick = {viewModel.onSelectRange(null)}, selected = dayFilter==null)}
        }
        Spacer(modifier=Modifier.height(20.dp))
        Text(
            text="Rol (Mostrando las que has)",
            style = MaterialTheme.typography.titleMedium
        )
        Row(modifier=Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start){
            FilterButton(label = "Escrito", onClick = {viewModel.onSelectTutor(false)}, selected = !isTutor)
            Spacer(modifier=Modifier.width(10.dp))
            FilterButton(label = "Recibido", onClick = {viewModel.onSelectTutor(true)}, selected = isTutor)
        }
        Spacer(modifier=Modifier.height(20.dp))
        PullToRefreshBox(isRefreshing = uiState.isLoading, onRefresh = {viewModel.reLoadReviews()}, modifier=Modifier.weight(1f).fillMaxWidth()) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), state = listState){
                if (uiState.error!="" && uiState.reviews.isEmpty() && !uiState.isLoading){
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        NoContentOrConnectionWidget(
                            modifier = Modifier.sizeIn(150.dp, 200.dp),
                            size = 100,
                            message = uiState.error,
                            text_style = MaterialTheme.typography.headlineSmall,
                            missingConnection = !connected
                        )
                    }
                }else{
                    if (uiState.reviews.isEmpty() && !uiState.isLoading){
                        item{
                            Spacer(modifier=Modifier.height(20.dp))
                            NoContentOrConnectionWidget(modifier = Modifier.sizeIn(150.dp, 200.dp), size = 100, message = "No se encontraron reseñas", text_style = MaterialTheme.typography.headlineSmall, missingConnection = !connected)
                        }
                    }else{
                        // MICRO-OPTIMIZACIÓN: Añadir key={ it.id } ayuda a Compose a evitar recomposiciones costosas.
                        items(items = uiState.reviews, key = { it.id }) { review ->
                            ReviewBanner(
                                modifier = Modifier.fillMaxWidth(),
                                review = review,
                                onClick = { clickedReview -> 
                                    com.uniandes.tutorias_g45k.data.local.ReviewLruCache.putReview(clickedReview)
                                    onReviewClick(clickedReview)
                                }
                            )
                        }
                    }
                }

            }
        }

    }
}

@Composable
fun FilterButton(modifier: Modifier = Modifier, label:String, onClick: () -> Unit, selected:Boolean){
    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = color),) {
        Text(text=label,
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
        )
    }
}

