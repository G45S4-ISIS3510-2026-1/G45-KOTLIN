package com.example.g45_kotlin.ui.reservation.summary.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.g45_kotlin.ui.theme.AppTheme
import com.example.g45_kotlin.utilities.generateQrCodeBitmap
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions


@Composable
fun QrBanner (modifier: Modifier = Modifier,
              isTutor: Boolean,
              qrContent: String = "www.google.com",
              verifScan: (String) -> Unit,
              scanResult:Boolean=false){
    ElevatedCard(modifier=modifier.clip(RoundedCornerShape(50.dp)), colors= CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.secondary,),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )) {
        if (isTutor) {
            TutorQrBanner(modifier = modifier, qrContent = qrContent)
        } else {
            var showDialog by remember { mutableStateOf(false) }
            var onDismiss by remember { mutableStateOf({}) }

            val resultTitle: String= when(scanResult){
                true -> "Asistencia Confirmada"
                false -> "Asistencia Rechazada"
            }
            val resultText: String = when(scanResult){
                true -> "Tu asistencia ha sido confirmada"
                false -> "Tu asistencia no ha sido confirmada, revisa el QR nuevamente"
            }

            val scanLauncher = rememberLauncherForActivityResult(
                contract = ScanContract(),
                onResult = { result ->
                    if (result.contents != null) {
                        verifScan(result.contents)
                    }
                    showDialog = true

                }
            )
            StudentQrBanner(modifier = modifier, onScan = {
                val options = ScanOptions()
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                options.setPrompt("Escanea el QR del tutor")
                options.setCameraId(0)
                options.setOrientationLocked(true)
                scanLauncher.launch(options)
            })

            if (showDialog) {
                ScanResultAlert(title = resultTitle, result = resultText, showDialog = showDialog, onDismiss = {showDialog = false})
            }
        }
    }
}

@Composable
fun StudentQrBanner (modifier: Modifier = Modifier,
                     onScan: () -> Unit) {
    Column(modifier=modifier.padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){
        Text(text="Confirma tu Asistencia",
            modifier=modifier,
            style=MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center
        )
        Text(text="Escanea el codigo QR de tu tutor para confirmar tu asistencia",
            modifier=modifier,
            style=MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Box(modifier=Modifier.padding(10.dp).fillMaxWidth(),
            contentAlignment = Alignment.Center
            ){
            Button(onClick = onScan) {
                Text(text = "Escanear QR",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }

}

@Composable
fun TutorQrBanner (modifier: Modifier = Modifier, qrContent: String = "www.google.com") {
    Column(modifier=modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){
        Text(text="¿Tu estudiante asistió a la tutoría?",
            modifier=modifier,
            style=MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Text(text="Presentale este codigo QR para confirmar su  asistencia",
            modifier=modifier,
            style=MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
    val codeBitmap = generateQrCodeBitmap(qrContent)
    Box(modifier=Modifier.padding(10.dp).fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){
        if (codeBitmap == null){
            Text(text="Error al generar el codigo QR, intenta reingresar a los detalles de tu reserva",
                modifier=modifier,
                style=MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
        else{
            Image(
                bitmap = codeBitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.size(200.dp).clip(RoundedCornerShape(50.dp))
            )
        }

    }


}

@Composable
fun ScanResultAlert(title:String, result:String, showDialog: Boolean, onDismiss: () -> Unit){
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            title = {
                Text(text = title)
            },
            text = {
                Text(text = result)
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }


@Composable
@Preview
fun QrBannerPreview(modifier: Modifier = Modifier){
    AppTheme() {
        QrBanner(modifier = modifier, isTutor = true, qrContent = "www.google.com", verifScan = {true})
    }
}
