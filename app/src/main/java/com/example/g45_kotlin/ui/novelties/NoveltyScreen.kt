package com.example.g45_kotlin.ui.novelties

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.g45_kotlin.data.novelty.NoveltyDto
import com.example.g45_kotlin.ui.NoContentOrConnectionWidget
import com.example.g45_kotlin.ui.novelties.components.NoveltyItem
import com.example.g45_kotlin.utilities.NetworkMonitor

@Composable
fun NoveltyScreen(modifier: Modifier = Modifier, viewModel: NoveltyViewModel = viewModel(), onNoveltyClick: (NoveltyDto) -> Unit = {}){
    val noveltyState by viewModel.noveltyState.collectAsStateWithLifecycle()
    val dayFilter by viewModel.dayFilter.collectAsStateWithLifecycle()
    val connected by NetworkMonitor.isOnline.collectAsStateWithLifecycle()
    val ranges=setOf(1,3,5,7)
    Column(modifier=modifier.fillMaxHeight().padding(20.dp)){
        Row(modifier=Modifier.fillMaxWidth().padding(top=30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Text(text="Novedades",
                modifier=Modifier.fillMaxWidth(fraction = 0.75f),
                style=MaterialTheme.typography.displayMedium
            )
        }
        if(!connected ){
            Column(){
                Text(
                    text="Sin conexión",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text="Actualmente no tiene conexión, por lo que no estas recibiendo nuevas novedades. Por favor revice y reestablezca la conexión para mantenerte al tanto",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                    )
            }
        }
        Spacer(modifier=Modifier.height(20.dp))
        LazyRow(modifier=Modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)){
            item{FilterButton(label = "Todas", onClick = {viewModel.onSelectRange(null)}, selected = dayFilter==null)}
            items(ranges.size){
                FilterButton(label = "Hace ${ranges.elementAt(it)} dia(s)", onClick = {viewModel.onSelectRange(ranges.elementAt(it))}, selected = dayFilter==ranges.elementAt(it))
            }
        }
        Spacer(modifier=Modifier.height(10.dp))

        PullToRefreshBox(isRefreshing = noveltyState.isLoading, onRefresh = {viewModel.reLoadNovelties()}, modifier=Modifier.weight(1f).fillMaxWidth()) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)){
                if (noveltyState.error!=null){
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        NoContentOrConnectionWidget(
                            modifier = Modifier.sizeIn(150.dp, 200.dp),
                            size = 100,
                            message = noveltyState.error ?: "Error desconocido",
                            text_style = MaterialTheme.typography.headlineSmall,
                            missingConnection = !connected
                        )
                    }
                }else{
                    if (noveltyState.novelties.isEmpty() && !noveltyState.isLoading){
                        item{
                            Spacer(modifier=Modifier.height(20.dp))
                            NoContentOrConnectionWidget(modifier = Modifier.sizeIn(150.dp, 200.dp), size = 100, message = "No se encontraron novedades pendientes", text_style = MaterialTheme.typography.headlineSmall, missingConnection = !connected)
                        }
                    }else{
                        items(noveltyState.novelties.size){index->
                            NoveltyItem(modifier = Modifier.fillMaxWidth(), novelty = noveltyState.novelties[index], onClick = onNoveltyClick, onDiscard = {viewModel.discardNovelty(it)})
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