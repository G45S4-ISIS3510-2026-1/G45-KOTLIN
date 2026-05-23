package com.uniandes.tutorias_g45k.ui.agenda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uniandes.tutorias_g45k.data.reservation.SessionDto
import com.uniandes.tutorias_g45k.ui.NoContentOrConnectionWidget
import com.uniandes.tutorias_g45k.ui.agenda.components.CalendarDaySelector
import com.uniandes.tutorias_g45k.ui.agenda.components.WeekDaySelector
import com.uniandes.tutorias_g45k.ui.profile.pages.reservations.SessionBanner
import com.uniandes.tutorias_g45k.ui.theme.AppTheme
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor
import com.uniandes.tutorias_g45k.utilities.getDaysOfCurrentCalendarWeek
import java.time.LocalDate

@Composable
fun ScheduleScreen(modifier: Modifier = Modifier,
                   weekDays:List<LocalDate> = getDaysOfCurrentCalendarWeek(),
                   onDaySelected: (LocalDate) -> Unit = {},
                   selectedDay: LocalDate = LocalDate.now(),
                   onPreviousWeek: () -> Unit = {},
                   onNextWeek: () -> Unit = {},
                   isLoading: Boolean = false,
                   onRefresh: () -> Unit = {},
                   sessions: List<SessionDto> = emptyList(),
                   currentUserId: String? = null,
                   onSessionClick: (String) -> Unit = {}
                   ) {
    val connected by NetworkMonitor.isOnline.collectAsState()
    Column(modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){

        CalendarDaySelector(modifier=Modifier.fillMaxWidth(),
            weekDays=weekDays,
            onDaySelected=onDaySelected,
            selectedDay=selectedDay,
            onPreviousWeek=onPreviousWeek,
            onNextWeek=onNextWeek,
            isLoading=isLoading
            )

        Spacer(modifier = Modifier.height(16.dp))
        if (!connected){
            Column(){
                Text(
                    text="Sin conexión",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text="Actualmente no tiene conexión, por lo que las sesiones mostradas podrían estar desactualizadas. Por favor revice su conexión y refresque",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = onRefresh
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ){
                if (sessions.isEmpty() && !isLoading){
                    item{
                        NoContentOrConnectionWidget(
                            size = 100,
                            missingConnection = !connected,
                            text_style = MaterialTheme.typography.headlineSmall,
                            message = if (connected) "Sin sesiones para mostrar" else "Sin internet. Revisa tu conexión y vuelve a intentarlo"
                        )
                    }
                }
                items(sessions.size){
                    index->
                    SessionBanner(
                        session = sessions[index],
                        onClick = onSessionClick,
                        currentUserId = currentUserId
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun ScheduleScreenPreview() {
    AppTheme(useSensor = false, darkTheme = true) {
        Surface(){
            ScheduleScreen ()
        }
    }
}