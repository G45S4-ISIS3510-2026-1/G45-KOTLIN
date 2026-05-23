package com.uniandes.tutorias_g45k.ui.agenda

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.tutorias_g45k.ui.theme.AppTheme
import kotlinx.coroutines.launch
import java.time.OffsetDateTime


@Composable
fun AgendaScreen(modifier: Modifier = Modifier, viewModel: AgendaViewModel = viewModel(), onSessionClick: (String) -> Unit = {} ) {
    val tabs = listOf("Horario", "Disponibilidad")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()


    val uiState by viewModel.uiState.collectAsState()



    Column(modifier = modifier.fillMaxSize().padding(20.dp)) {
        Text(text="Agenda",
            modifier=Modifier.fillMaxWidth(fraction = 0.75f).padding(top=30.dp),
            style=MaterialTheme.typography.displayMedium
        )



        SecondaryTabRow(
            modifier=Modifier.fillMaxWidth().padding(10.dp),
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(text = title,
                        style = MaterialTheme.typography.titleMedium) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalAlignment = Alignment.Top
        ) { page ->
            when (page) {
                0 -> ScheduleScreen(
                    weekDays = uiState.selectedWeek,
                    selectedDay = uiState.selectedDay,
                    onDaySelected = { viewModel.selectDate(it) },
                    onPreviousWeek = { viewModel.getPreviousWeek() },
                    onNextWeek = { viewModel.getNextWeek()},
                    isLoading = uiState.isLoading,
                    onRefresh = { viewModel.fetchSessions() },
                    sessions = uiState.sessions,
                    currentUserId =uiState.currentUserId,
                    onSessionClick = onSessionClick
                )
                1 -> AvailabilityScreen(
                    onSelectWeekDay = { viewModel.selectWeekDay(it) },
                    selectedHours = uiState.hours,
                    selectedWeekDay = uiState.selectedWeekDay,
                    isTutor = uiState.isTutor
                )
            }
        }
    }
}




@Preview()
@Composable
fun DashboardTabsScreenPreview() {
    AppTheme(true, useSensor = false){
        Surface(){
            AgendaScreen()
        }
    }
}