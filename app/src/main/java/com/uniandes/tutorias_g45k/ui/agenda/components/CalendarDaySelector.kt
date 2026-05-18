package com.uniandes.tutorias_g45k.ui.agenda.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uniandes.tutorias_g45k.ui.theme.AppTheme
import com.uniandes.tutorias_g45k.utilities.getDaysOfCertainWeek
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarDaySelector(modifier: Modifier =Modifier,
                    weekDays:List<LocalDate> = getDaysOfCertainWeek(LocalDate.now()),
                    onDaySelected: (LocalDate) -> Unit={},
                    selectedDay: LocalDate = LocalDate.now(),
                    onPreviousWeek: () -> Unit = {},
                    onNextWeek: () -> Unit = {},
                    isLoading: Boolean = false){
    val monthName = selectedDay.month.getDisplayName(
        TextStyle.FULL,
        Locale.forLanguageTag("es-CO")
    ).replaceFirstChar { it.uppercase() }

    var showCalendar by remember { mutableStateOf(false) }

    CalendarPicker(currentDay = selectedDay, onDateSelected = onDaySelected, showDialog = showCalendar, onDismiss = {showCalendar=false})

    Column(modifier=modifier.fillMaxWidth(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally){
        Row(modifier=Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start){
            Text(
                text = "$monthName, ${selectedDay.year}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier=Modifier.size(60.dp)
                    .dropShadow(
                        shape = CircleShape,
                        shadow = Shadow(
                            radius = 10.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                        )
                    )
                    .padding(5.dp),
                shape= CircleShape,
                onClick={showCalendar=true}
            ){
                Icon(
                    modifier=Modifier.requiredSize(45.dp)
                        .padding(5.dp),
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Agenda calendar",
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ){
            Icon(
                modifier=Modifier.requiredSize(25.dp)
                    .padding(5.dp)
                    .clickable(onClick=onPreviousWeek, enabled = !isLoading),
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Previous week",
            )
            AnimatedContent(
                targetState = weekDays,
                transitionSpec = {
                    if (targetState.first().isAfter(initialState.first())) {
                        (slideInHorizontally(animationSpec = tween(300)) { width -> width } + fadeIn()) togetherWith
                                (slideOutHorizontally(animationSpec = tween(300)) { width -> -width } + fadeOut())
                    } else {
                        (slideInHorizontally(animationSpec = tween(300)) { width -> -width } + fadeIn()) togetherWith
                                (slideOutHorizontally(animationSpec = tween(300)) { width -> width } + fadeOut())
                    }
                },
                modifier = Modifier.weight(1f), // Para que tome el espacio entre botones
                label = "WeekAnimation"
            ) { animatedWeekDays ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround){
                    animatedWeekDays.forEach { day ->
                        DaySelector(
                            date = day,
                            selected = day == selectedDay,
                            onClick = {onDaySelected(day)},
                            enabled = !isLoading
                        )
                    }
                }
            }

            Icon(
                modifier=Modifier.requiredSize(25.dp)
                    .padding(5.dp)
                    .clickable(onClick=onNextWeek, enabled = !isLoading),
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Next week",
            )
        }

    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarPicker(currentDay: LocalDate = LocalDate.now(),
                   onDateSelected: (LocalDate) -> Unit = {},
                   showDialog: Boolean = true,
                   onDismiss: () -> Unit = {}) {
    val initial=when{
        currentDay.dayOfWeek==DayOfWeek.SUNDAY -> currentDay.minusDays(1)
        currentDay.dayOfWeek==DayOfWeek.SATURDAY -> currentDay.minusDays(2)
        else -> currentDay
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initial.toEpochDay() * 24 * 60 * 60 * 1000,
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()

                return date.dayOfWeek != DayOfWeek.SUNDAY
            }
            override fun isSelectableYear(year: Int): Boolean {
                // Todos los años son seleccionables (o puedes restringirlos aquí también)
                return true
            }
        }
    )

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(LocalDate.ofEpochDay(
                    datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate().toEpochDay()
                    } ?: currentDay.toEpochDay()
                    ));
                    onDismiss()
                }
                ) {
                    Text("OK",
                        style = MaterialTheme.typography.titleMedium)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancelar",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                title = {
                    Text(
                        text = "Seleccione fecha",
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                headline= {
                    val dateText = datePickerState.selectedDateMillis?.let {
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).format(formatter)
                    } ?: ""
                    Text(
                        text = dateText,
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                    selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                    todayDateBorderColor = MaterialTheme.colorScheme.primary,
                    subheadContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
    }
}

@Preview
@Composable
fun MyDatePickerPreview() {
    AppTheme(useSensor = false, darkTheme = true) {
        Surface(){
            CalendarDaySelector()
        }
    }
}

@Composable
fun DaySelector(modifier: Modifier = Modifier,
                date: LocalDate,
                selected:Boolean,
                onClick:() -> Unit,
                enabled:Boolean=true){
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val borderWidth = if (selected) 2.dp else 0.dp

    Surface(modifier=modifier
        .requiredHeight(100.dp)
        .widthIn(min = 50.dp)
        .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor ,
        border = BorderStroke(width = borderWidth, color = MaterialTheme.colorScheme.onPrimary),
        tonalElevation = 4.dp,
        shadowElevation = 5.dp){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es", "CO")).uppercase(),
                style=MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = modifier.height(8.dp))
            Text(
                text = date.dayOfMonth.toString(),
                style=MaterialTheme.typography.headlineSmall,
            )
        }
    }
}
