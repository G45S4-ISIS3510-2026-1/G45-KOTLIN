package com.example.g45_kotlin.ui.tutor.become

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BecomeTutorScheduleScreen(
    viewModel: BecomeTutorViewModel,
    onPublish: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val days = listOf("LUN", "MAR", "MIE", "JUE", "VIE", "SAB", "DOM")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar Horario") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = onPublish,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = uiState.availability.values.any { it.isNotEmpty() }
            ) {
                Text("Publicar Perfil")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = days.indexOf(uiState.selectedDay),
                edgePadding = 16.dp
            ) {
                days.forEach { day ->
                    Tab(
                        selected = uiState.selectedDay == day,
                        onClick = { viewModel.selectDay(day) },
                        text = { Text(day) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val currentSlots = uiState.availability[uiState.selectedDay] ?: emptyList()
                
                items(currentSlots) { slot ->
                    TimeSlotItem(
                        slot = slot,
                        onUpdate = { from, to -> 
                            viewModel.updateTimeSlot(uiState.selectedDay, slot.id, from, to)
                        },
                        onRemove = { 
                            viewModel.removeTimeSlot(uiState.selectedDay, slot.id)
                        }
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { viewModel.addTimeSlot(uiState.selectedDay) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Agregar Franja")
                    }
                }
            }
        }
    }
}

@Composable
fun TimeSlotItem(
    slot: TimeSlot,
    onUpdate: (String, String) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var fromText by remember { mutableStateOf(slot.from) }
            var toText by remember { mutableStateOf(slot.to) }

            OutlinedTextField(
                value = fromText,
                onValueChange = { 
                    fromText = it
                    onUpdate(it, toText)
                },
                label = { Text("Desde") },
                modifier = Modifier.weight(1f)
            )
            
            OutlinedTextField(
                value = toText,
                onValueChange = { 
                    toText = it
                    onUpdate(fromText, it)
                },
                label = { Text("Hasta") },
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
