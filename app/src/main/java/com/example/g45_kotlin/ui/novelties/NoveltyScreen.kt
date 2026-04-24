package com.uniandes.tutorias_g45k.ui.novelties

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.tutorias_g45k.data.novelty.NoveltyDto
import com.uniandes.tutorias_g45k.ui.NoContentOrConnectionWidget
import com.uniandes.tutorias_g45k.ui.novelties.components.NoveltyItem

@Composable
fun NoveltyScreen(modifier: Modifier = Modifier, viewModel: NoveltyViewModel = viewModel(), onNoveltyClick: (NoveltyDto) -> Unit = {}){
    val noveltyState by viewModel.noveltyState.collectAsStateWithLifecycle()
    val dayFilter by viewModel.dayFilter.collectAsStateWithLifecycle()
    val ranges=setOf(1,3,5,7)
    Column(modifier=modifier.fillMaxHeight().padding(20.dp)){
        Spacer(modifier=Modifier.height(10.dp))
        Row(modifier=Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Text(text="Novedades",
                modifier=Modifier.fillMaxWidth(fraction = 0.75f),
                style=MaterialTheme.typography.displayMedium
            )
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
                        )
                    }
                }else{
                    if (noveltyState.novelties.isEmpty() && !noveltyState.isLoading){
                        item{
                            Spacer(modifier=Modifier.height(20.dp))
                            NoContentOrConnectionWidget(modifier = Modifier.sizeIn(150.dp, 200.dp), size = 100, message = "No se encontraron novedades pendientes")
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
