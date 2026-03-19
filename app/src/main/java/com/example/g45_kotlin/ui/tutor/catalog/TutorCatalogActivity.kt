package com.example.g45_kotlin.ui.tutor.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.g45_kotlin.ui.theme.G45KOTLINTheme

class TutorCatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            G45KOTLINTheme {
                CatalogoScreen()
            }
        }
    }
}

@Composable
fun CatalogoScreen(viewModel: TutorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.selectedTutor != null) {
        TutorDetailScreen(
            tutor = uiState.selectedTutor!!,
            onBack = { viewModel.onTutorSelected(null) }
        )
        BackHandler {
            viewModel.onTutorSelected(null)
        }
    } else {
        CatalogoContent(
            uiState = uiState,
            onSearchTextChange = { viewModel.onSearchTextChange(it) },
            onOrderChange = { viewModel.onOrderChange(it) },
            onFacultadChange = { viewModel.onFacultadChange(it) },
            onTutorClick = { viewModel.onTutorSelected(it) }
        )
    }
}
