package com.uniandes.tutorias_g45k.ui.profile.pages.pqrs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.tutorias_g45k.ui.NoContentOrConnectionWidget
import com.uniandes.tutorias_g45k.utilities.GoogleAnalyticsService
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PqrListScreen(
    modifier: Modifier = Modifier,
    viewModel: PqrListViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val connected by NetworkMonitor.isOnline.collectAsState()

    LaunchedEffect(Unit) {
        GoogleAnalyticsService.logScreenAccess("PqrList")
    }

    Column(modifier = modifier.fillMaxHeight().padding(20.dp)) {
        Spacer(modifier = Modifier.height(30.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack, 
                modifier = Modifier.requiredSize(50.dp), 
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier.size(25.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Historial PQRs",
                style = MaterialTheme.typography.displayMedium
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // EVENTUAL CONNECTIVITY BANNER
        if (!connected) {
            Column {
                Text(
                    text = "Modo Offline",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "No hay conexión a internet. Mostrando PQRs guardados en la caché local de Firestore.",
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        PullToRefreshBox(
            isRefreshing = uiState.isLoading, 
            onRefresh = { viewModel.loadPqrs() }, 
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (uiState.error.isNotBlank() && uiState.pqrs.isEmpty() && !uiState.isLoading) {
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
                } else if (uiState.pqrs.isEmpty() && !uiState.isLoading) {
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        NoContentOrConnectionWidget(
                            modifier = Modifier.sizeIn(150.dp, 200.dp),
                            size = 100,
                            message = "No tienes PQRs radicados",
                            text_style = MaterialTheme.typography.headlineSmall,
                            missingConnection = !connected
                        )
                    }
                } else {
                    items(items = uiState.pqrs, key = { it.id }) { pqr ->
                        PqrBanner(
                            modifier = Modifier.fillMaxWidth(),
                            pqr = pqr
                        )
                    }
                }
            }
        }
    }
}
