package com.uniandes.tutorias_g45k.ui.tutor.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.uniandes.tutorias_g45k.data.recommendation.TutorSummaryDto
import com.uniandes.tutorias_g45k.ui.NoContentOrConnectionWidget
import com.uniandes.tutorias_g45k.ui.theme.AppTheme
import com.uniandes.tutorias_g45k.utilities.GoogleAnalyticsService
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoContent(
    uiState: CatalogoUiState,
    onRetry: () -> Unit = {},
    onLoadNextPage: () -> Unit = {},
    onSearchTextChange: (String) -> Unit,
    onOrderChange: (String) -> Unit,
    onFacultadChange: (String) -> Unit,
    onOnlyFavoritesChange: (Boolean) -> Unit = {},
    onTutorClick: (TutorSummaryDto) -> Unit
) {
    val connected by NetworkMonitor.isOnline.collectAsState()
    LaunchedEffect(Unit){
        GoogleAnalyticsService.logScreenAccess("TutorCatalog", screenClass="TutorCatalog")
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { onRetry() },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.padding(top = 48.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = "Catálogo",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = { onOnlyFavoritesChange(!uiState.onlyFavorites) },
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (uiState.onlyFavorites) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = if (uiState.onlyFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Ver Favoritos",
                                tint = if (uiState.onlyFavorites) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = uiState.searchText,
                        onValueChange = onSearchTextChange,
                        placeholder = {
                            Text(
                                text = "Buscar por materia o tutor...",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                    )
                }

                item {
                    FilterSection(
                        title = "ORDENAR POR",
                        options = listOf("Mejor Rating", "Precio"),
                        selectedOption = uiState.selectedOrder,
                        onOptionSelected = onOrderChange
                    )
                }
                item {
                    FilterSection(
                        title = "FACULTADES",
                        options = listOf("Todas", "Ingeniería", "Ciencias", "Economía", "Artes"),
                        selectedOption = uiState.selectedFacultad,
                        onOptionSelected = onFacultadChange
                    )
                }

                if ((uiState.error != null) && uiState.visibleTutores.isEmpty()) {
                    item {
                        NoContentOrConnectionWidget(modifier=Modifier, size=100, text_style=MaterialTheme.typography.titleLarge, message= uiState.error, missingConnection = !connected)
                    }
                } else if (uiState.visibleTutores.isEmpty() && !uiState.isLoading) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (uiState.onlyFavorites) "No tienes tutores favoritos aún" else "No se encontraron tutores",
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    itemsIndexed(uiState.visibleTutores) { index, tutor ->
                        if (index >= uiState.visibleTutores.size - 1) {
                            LaunchedEffect(index) {
                                onLoadNextPage()
                            }
                        }
                        TutorCard(tutor, onClick = { onTutorClick(tutor) })
                    }

                    if (uiState.isLoading && uiState.visibleTutores.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
fun FilterSection(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(options) { option ->
                val isSelected = option == selectedOption
                SuggestionChip(
                    onClick = {
                        GoogleAnalyticsService.logButtonClick(option, "TutorCatalog")
                        onOptionSelected(option) },
                    label = {
                        Text(
                            text = option,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    ),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
fun TutorCard(tutor: TutorSummaryDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(contentAlignment = Alignment.BottomCenter) {
                    if (tutor.profileImageUrl.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(color = MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tutor.name.take(1).uppercase(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    } else {
                        AsyncImage(
                            model = tutor.profileImageUrl,
                            contentDescription = "Foto de ${tutor.name}",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.offset(y = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.Yellow,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = tutor.rating.toString(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tutor.name,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$ ${tutor.sessionPrice?.div(1000)}k/h",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.alignByBaseline()
                        )
                    }
                    Text(
                        text = "${tutor.major} (${mapearFacultad(tutor.major)})",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "VER DETALLES",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CatalogoPreview() {
    AppTheme {
        val mockState = CatalogoUiState(
            tutores = listOf(
                TutorSummaryDto("1", "Preview Tutor", "Carrera", 4.5, "", 10000),
                TutorSummaryDto("1", "Preview Tutor", "Carrera", 4.5, "", 10000)
            ),
            filtrados = listOf(
                TutorSummaryDto("1", "Preview Tutor", "Carrera", 4.5, "", 10000),
                TutorSummaryDto("1", "Preview Tutor", "Carrera", 4.5, "", 10000)

            )
        )
        CatalogoContent(
            uiState = mockState,
            onSearchTextChange = {},
            onOrderChange = {},
            onFacultadChange = {},
            onTutorClick = {}
        )
    }
}

private fun mapearFacultad(major: String): String {
    return when {
        major.contains("Ingeniería", ignoreCase = true) -> "Ingeniería"
        major.contains("Matemáticas", ignoreCase = true) || major.contains("Física", ignoreCase = true) -> "Ciencias"
        major.contains("Economía", ignoreCase = true) || major.contains("Administración", ignoreCase = true) -> "Economía"
        major.contains("Artes", ignoreCase = true) || major.contains("Diseño", ignoreCase = true) -> "Artes"
        else -> "Otras"
    }
}