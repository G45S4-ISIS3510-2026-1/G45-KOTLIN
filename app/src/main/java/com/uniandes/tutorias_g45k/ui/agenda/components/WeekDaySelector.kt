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
fun WeekDaySelector(modifier: Modifier =Modifier,
                    onDaySelected: (Int) -> Unit={},
                    selectedDay: Int = 0,){
    val days= listOf(0,1,2,3,4,5)

    Column(modifier=modifier.fillMaxWidth(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ){
            days.forEach{
                day->
                DayBanner(
                    day = day,
                    selected = day == selectedDay,
                    onClick = {onDaySelected(day)},
                )
            }
        }
    }
}


@Composable
fun DayBanner(modifier: Modifier = Modifier,
                day: Int,
                selected:Boolean,
                onClick:() -> Unit){
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val borderWidth = if (selected) 2.dp else 0.dp

    val weekDay=DayOfWeek.of(day+1).getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("es-CO"))

    Surface(modifier=modifier
        .requiredHeight(100.dp)
        .widthIn(min = 50.dp)
        .clickable(onClick = onClick),
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
                text = weekDay.uppercase(),
                style=MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
@Preview
fun WeekDaySelectorPreview() {
    AppTheme(useSensor = false, darkTheme = true) {
        Surface(){
            WeekDaySelector()
        }
    }
}
