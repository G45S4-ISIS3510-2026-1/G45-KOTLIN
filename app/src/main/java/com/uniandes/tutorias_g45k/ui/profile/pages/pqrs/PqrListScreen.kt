package com.uniandes.tutorias_g45k.ui.profile.pages.pqrs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.uniandes.tutorias_g45k.R
import com.uniandes.tutorias_g45k.ui.NoContentOrConnectionWidget
import com.uniandes.tutorias_g45k.ui.profile.pages.reservations.FilterButton
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor
import java.text.SimpleDateFormat
import java.util.Locale

private val dayRanges = listOf(1, 7, 15, 30)
private val dayRangeLabels = mapOf(1 to "Hoy", 7 to "7 días", 15 to "15 días", 30 to "30 días")
private val pqrStatuses = listOf("Pendiente", "En Revisión", "Concluida", "Cancelada")

@Composable
fun PqrListScreen(
    onBack: () -> Unit,
    onCreatePqr: () -> Unit,
    viewModel: PqrListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dayFilter by viewModel.dayFilter.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val isOnline by NetworkMonitor.isOnline.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePqr,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Reporte")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxHeight()
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier.requiredSize(50.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        modifier = Modifier.requiredSize(25.dp).fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Reportes",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.displayMedium,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!isOnline) {
                Column {
                    Text(
                        text = "Sin conexión",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "No hay conexión a internet. Mostrando PQRs guardados en caché local.",
                        textAlign = TextAlign.Justify,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            Text(text = "Temporalidad", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    FilterButton(
                        label = "Todas",
                        onClick = { viewModel.onSelectDayRange(null) },
                        selected = dayFilter == null
                    )
                }
                items(dayRanges) { range ->
                    FilterButton(
                        label = dayRangeLabels[range] ?: "$range días",
                        onClick = { viewModel.onSelectDayRange(range) },
                        selected = dayFilter == range
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Estado", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    FilterButton(
                        label = "Todos",
                        onClick = { viewModel.onSelectStatus(null) },
                        selected = statusFilter == null
                    )
                }
                items(pqrStatuses) { status ->
                    FilterButton(
                        label = status,
                        onClick = { viewModel.onSelectStatus(status) },
                        selected = statusFilter == status
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = { viewModel.fetchPqrs() },
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (uiState.error != null && uiState.displayItems.isEmpty() && !uiState.isLoading) {
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            NoContentOrConnectionWidget(
                                modifier = Modifier.sizeIn(150.dp, 200.dp),
                                size = 100,
                                message = uiState.error!!,
                                text_style = MaterialTheme.typography.headlineSmall,
                                missingConnection = !isOnline
                            )
                        }
                    } else if (uiState.displayItems.isEmpty() && !uiState.isLoading) {
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            NoContentOrConnectionWidget(
                                modifier = Modifier.sizeIn(150.dp, 200.dp),
                                size = 100,
                                message = "No se encontraron reportes",
                                text_style = MaterialTheme.typography.headlineSmall,
                                missingConnection = !isOnline
                            )
                        }
                    } else {
                        items(uiState.displayItems) { item ->
                            PqrItem(modifier = Modifier.fillMaxWidth(), item = item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PqrItem(modifier: Modifier = Modifier, item: PqrDisplayItem) {
    val pqr = item.pqr
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateStr = pqr.createdAt?.let { sdf.format(it.toDate()) } ?: "Fecha desconocida"

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            if (!item.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.profile_placeholder),
                    fallback = painterResource(R.drawable.profile_placeholder)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pqr.topic?.take(1)?.uppercase() ?: "?",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pqr.topic?.ifBlank { "Sin Asunto" } ?: "Sin Asunto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    PqrStatusChip(status = pqr.status ?: "Pendiente")
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pqr.description ?: "Sin descripción",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = pqr.type ?: "N/A",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun PqrStatusChip(status: String) {
    val (containerColor, contentColor) = when (status.lowercase()) {
        "pendiente"    -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "en revisión"  -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        "concluida"    -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        else           -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
