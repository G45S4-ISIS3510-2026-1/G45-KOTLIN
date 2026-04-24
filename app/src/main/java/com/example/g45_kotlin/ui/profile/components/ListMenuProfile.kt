package com.example.g45_kotlin.ui.profile.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.g45_kotlin.ui.theme.AppTheme

@Composable
fun ListMenuProfile(modifier: Modifier = Modifier,
                    onCheckReservations:()->Unit={},
                    onEditProfile:()->Unit={},
                    onCheckPQRs:()->Unit={},
                    onCheckFavorites:()->Unit={},
                    onChangePreference:()->Unit={},
                    onCheckReviews:()->Unit={},
                    dynamicTheme:Boolean=false
){


    Surface(modifier=modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(35.dp),
    ){
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally){
            EditThemeOption(modifier = Modifier.fillMaxWidth(), onThemeChange = onChangePreference, dynamicTheme = dynamicTheme)
            HorizontalDivider(thickness = 10.dp, color = MaterialTheme.colorScheme.surface)
            NavOption(modifier = Modifier.fillMaxWidth(), label = "Historial de Sesiones", onClick = onCheckReservations)
            HorizontalDivider(thickness = 10.dp, color = MaterialTheme.colorScheme.surface)
            NavOption(modifier = Modifier.fillMaxWidth(), label = "Reseñas", onClick = onCheckReviews)
            HorizontalDivider(thickness = 10.dp, color = MaterialTheme.colorScheme.surface)
            NavOption(modifier = Modifier.fillMaxWidth(), label = "Tutores Favoritos", onClick = onCheckFavorites)
            HorizontalDivider(thickness = 10.dp, color = MaterialTheme.colorScheme.surface)
            NavOption(modifier = Modifier.fillMaxWidth(), label = "Editar Datos de Perfil/Tutor", onClick = onEditProfile)
            HorizontalDivider(thickness = 10.dp, color = MaterialTheme.colorScheme.surface)
            NavOption(modifier = Modifier.fillMaxWidth(), label = "Peticiones, Quejas, y Reclamos", onClick = onCheckPQRs)
        }
    }
}

@Composable
fun EditThemeOption(modifier: Modifier = Modifier, onThemeChange:()->Unit, dynamicTheme:Boolean=false ){
    var expanded by remember { mutableStateOf(false) }
    val expandIcon = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore
    Card(modifier = modifier.animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ){
        Surface(modifier = Modifier.padding(20.dp).fillMaxWidth(), color = Color.Transparent){
            Column(modifier=Modifier.fillMaxWidth()){
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start){
                    Column(modifier = Modifier, verticalArrangement = Arrangement.SpaceEvenly){
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = "Expand Icon",
                            modifier = Modifier.requiredSize(30.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier, verticalArrangement = Arrangement.SpaceEvenly){
                        Text(
                            text = "Personalizar Tema",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(modifier = Modifier, verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally){
                        Icon(
                            imageVector = expandIcon,
                            contentDescription = "Expand Icon",
                            modifier = Modifier.requiredSize(25.dp).clickable { expanded = !expanded },
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (expanded){
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                        val color=if (dynamicTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                        val textColor=if (dynamicTheme) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onTertiary
                        val buttonText=if (dynamicTheme) "Dinámico" else "Dispositivo"
                        val message=if (dynamicTheme)
                            "El tema dinámico reacciona en función de la luz de tu entorno, variando entre el tema claro en zona luminadas y el tema oscuro en zonas con poca luz. En caso de que su dispositivo no tenga sensor de luz, este funcionará igual que el tema de dispositivo."
                            else "El tema de dispositivo actua en función de la configuración predeterminada de su dispositivo, siendo equivalente al tema dinámico si su dispositivo no tiene sensor de luz activo."
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                            Button(modifier=Modifier.requiredWidth(150.dp), onClick=onThemeChange, colors = ButtonDefaults.buttonColors(containerColor = color)){
                                Text(text = buttonText, color = textColor, style = MaterialTheme.typography.titleMedium)
                            }
                            Text(
                                text = message,
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Justify,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }


        }
    }
}

@Composable
fun NavOption(modifier: Modifier = Modifier, label:String, onClick:()->Unit={}){
    val icon=when(label){
        "Historial de Sesiones"->Icons.Default.History
        "Reseñas"->Icons.Default.StarRate
        "Tutores Favoritos"->Icons.Default.Favorite
        "Editar Datos de Perfil/Tutor"->Icons.Default.Edit
        "Peticiones, Quejas, y Reclamos"-> Icons.AutoMirrored.Filled.Help
        else->Icons.Default.ColorLens
    }
    Card(modifier = modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ){
        Surface(modifier = Modifier.padding(20.dp).fillMaxWidth(), color = Color.Transparent) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically){
                Column(modifier = Modifier, verticalArrangement = Arrangement.SpaceEvenly){
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.requiredSize(30.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.fillMaxWidth(fraction = 0.8f), verticalArrangement = Arrangement.SpaceEvenly){
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }
                Spacer(modifier = Modifier.weight(1f))
                Column(modifier = Modifier, verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally){
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                        contentDescription = "Expand Icon",
                        modifier = Modifier.requiredSize(25.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun ListMenuProfileMenu(){
    var dynamicTheme by remember { mutableStateOf(true) }
    AppTheme(useSensor = false, darkTheme = true){
        Surface(modifier = Modifier) {
            ListMenuProfile(
                modifier = Modifier.fillMaxWidth(),
                onChangePreference = { dynamicTheme = !dynamicTheme },
                dynamicTheme = dynamicTheme
            )
        }
    }
}
