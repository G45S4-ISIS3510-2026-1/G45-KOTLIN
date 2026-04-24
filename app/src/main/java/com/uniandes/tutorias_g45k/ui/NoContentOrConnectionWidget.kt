package com.uniandes.tutorias_g45k.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uniandes.tutorias_g45k.R

@Composable
fun NoContentOrConnectionWidget(modifier: Modifier =Modifier, size: Int, text_style: TextStyle =MaterialTheme.typography.headlineMedium, message:String="No hay contenido disponible", missingConnection:Boolean=false){
    Column(modifier=modifier, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
        Icon(
            painter=if (missingConnection) painterResource(R.drawable.no_connection_icon) else painterResource(R.drawable.empty_icon),
            contentDescription = if (missingConnection) "No hay conexion" else "No hay contenido",
            modifier = Modifier.requiredSize(size.dp),
            tint=MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier=Modifier.height(10.dp))
        Text(
            text = message,
            modifier = Modifier.fillMaxWidth(),
            style = text_style,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview
@Composable
fun NoContentOrConnectionWidgetPreview(){
    Surface(){
        NoContentOrConnectionWidget(modifier = Modifier.fillMaxSize(), size = 200, missingConnection = true)
    }

}


