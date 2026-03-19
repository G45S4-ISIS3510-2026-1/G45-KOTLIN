package com.example.g45_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.g45_kotlin.ui.theme.G45KOTLINTheme
import com.example.g45_kotlin.ui.tutor.catalog.CatalogoScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            G45KOTLINTheme {
                G45KOTLINApp()
            }
        }
    }
}

@Composable
fun G45KOTLINApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.CATALOGO) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        when (currentDestination) {
            AppDestinations.CATALOGO -> {
                CatalogoScreen()
            }
            AppDestinations.FAVORITES -> {
                PlaceholderScreen("Favoritos")
            }
            AppDestinations.PROFILE -> {
                PlaceholderScreen("Perfil")
            }
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Greeting(
            name = name,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    CATALOGO("Catálogo", Icons.Default.Search),
    FAVORITES("Favoritos", Icons.Default.Favorite),
    PROFILE("Perfil", Icons.Default.AccountBox),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Pantalla de $name",
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    G45KOTLINTheme {
        Greeting("Catálogo")
    }
}
