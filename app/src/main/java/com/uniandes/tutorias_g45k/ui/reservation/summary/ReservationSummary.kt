package com.uniandes.tutorias_g45k.ui.reservation.summary


import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.tutorias_g45k.ui.CommonErrorDialog
import com.uniandes.tutorias_g45k.ui.LoadingDialog
import com.uniandes.tutorias_g45k.ui.reservation.summary.components.DetailsBanner
import com.uniandes.tutorias_g45k.ui.reservation.summary.components.Participants
import com.uniandes.tutorias_g45k.ui.reservation.summary.components.QrBanner
import com.uniandes.tutorias_g45k.ui.theme.AppTheme
import com.uniandes.tutorias_g45k.utilities.GoogleAnalyticsService
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor
import java.time.LocalDateTime

@Composable
fun ReservationSummary(
    modifier: Modifier = Modifier,
    viewModel: ReservationSummaryViewModel = viewModel(),
    onBack:()->Unit={}
) {


    val summaryState by viewModel.summaryState.collectAsState()
    val connected by NetworkMonitor.isOnline.collectAsState()

    LaunchedEffect(Unit){
        GoogleAnalyticsService.logScreenAccess("ReservationSummary")
    }

    if (summaryState.fetchError!=null){
        if (summaryState.fetchError!!.contains("cargar")){
            CommonErrorDialog(message = summaryState.fetchError!!, onDismiss = {onBack()})
        }else{
            CommonErrorDialog(message = summaryState.fetchError!!, onDismiss = {})
        }
    }

    Column(modifier=modifier.padding(10.dp)){
        Spacer(modifier=modifier.height(10.dp))
        Row(modifier=modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Button(onClick = { onBack() }, modifier=modifier.requiredSize(50.dp), shape= CircleShape) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "GoBack",
                    modifier = Modifier
                        .requiredSize(25.dp)
                        .fillMaxSize()
                )
            }
            Spacer(modifier=Modifier.width(10.dp))
            Text(text="Detalles de Reserva",
                modifier=modifier.fillMaxWidth(fraction = 0.75f),
                style=MaterialTheme.typography.displaySmall
            )
        }
        if (!connected){
            Spacer(modifier=modifier.height(20.dp))
            Column(){
                Text(
                    text="Sin conexión",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text="Los detalles presentes en esta tutoría podrían estar desactualizados, por lo que modificacion como la cancelación y/o confirmación estan deshabilitados. Por favor revisa tu conexión y vuelve a ingresar para actualizar la información.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )

            }

        }
        Box(modifier=Modifier.fillMaxSize()){
            //Content
            LazyColumn(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ){
                item{Spacer(modifier=Modifier.height(50.dp))}
                val date= summaryState.date
                val skill= summaryState.skill
                item {DetailsBanner(modifier=Modifier.fillMaxWidth(), date= date, skill=skill)}
                val studentName=summaryState.student.name
                val studentPicture=summaryState.student.picture
                val tutorName=summaryState.tutor.name
                val tutorPicture=summaryState.tutor.picture
                item {Participants(modifier=Modifier.fillMaxWidth(), tutorName=tutorName, tutorPicture=tutorPicture, studentName=studentName, studentPicture=studentPicture)}
                //QR
                val status=summaryState.status
                val isTutor=viewModel.tutorSite()
                val qrContent=summaryState.qrContent
                if (status.status=="Pendiente"){
                    item{QrBanner(modifier=Modifier.fillMaxWidth(), isTutor=isTutor, qrContent=qrContent, verifScan=viewModel::verifyScanCode, connected=connected)}
                    item{Spacer(modifier=Modifier.height(200.dp))}
                }
            }
            Surface(shape = RoundedCornerShape(bottomEnd = 25.dp, bottomStart = 25.dp),
                ){
                StatusBanner(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp), status=summaryState.status)
            }
            //Buttons
            fun enabled():Boolean{
                val todayTime = LocalDateTime.now()
                val twentyFourHoursFromNow = summaryState.date.minusHours(24)
                return summaryState.status==Status.PENDING && todayTime.isBefore(twentyFourHoursFromNow)
            }
            val isEnabled=enabled() && connected
            Surface(modifier=Modifier.align(Alignment.BottomCenter),
                shape = RoundedCornerShape(topEnd = 50.dp, topStart = 50.dp),
            ){
                Column(){
                    var showconfirm by remember { mutableStateOf(false) }
                    if (showconfirm){
                        CancelConfirmationDialog(modifier=Modifier.fillMaxWidth().padding(top=20.dp), onConfirm = { viewModel.cancelReservation(); showconfirm=false}, onDismiss = {showconfirm=false})
                    }
                    CancelAcceptButtons(modifier=Modifier.fillMaxWidth().padding(top=20.dp), onCancel = { showconfirm=true }, onAccept = { onBack() }, enabled=isEnabled)
                    if (!isEnabled && summaryState.status==Status.PENDING){
                        Text(text="Cancelacion deshabilitada. Recuerda cancelar con mínimo 1 día de anticipación",
                            modifier=Modifier.fillMaxWidth().padding(bottom = 50.dp, top=20.dp),
                            textAlign = TextAlign.Center,
                            style=MaterialTheme.typography.bodyLarge,
                            color=MaterialTheme.colorScheme.error
                        )
                    }
                }

            }

        }

    }
    LoadingDialog(modifier=Modifier, show = summaryState.isLoading)
    if (summaryState.qrResult!=null || summaryState.qrTitleResult!=null){
        ScanResultAlert(title=summaryState.qrTitleResult!!, result=summaryState.qrResult!!, showDialog = true, onDismiss = viewModel::clearResult)
    }
}

@Composable
fun StatusBanner(modifier: Modifier=Modifier, status:Status){
    Surface(Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50.dp),
        color = when(status){
            Status.PENDING -> MaterialTheme.colorScheme.primary
            Status.CONCLUDED -> MaterialTheme.colorScheme.tertiary
            Status.CANCELLED -> MaterialTheme.colorScheme.error
            Status.OVERDUE -> MaterialTheme.colorScheme.error
        },
        border = BorderStroke(width = 7.dp, color = MaterialTheme.colorScheme.tertiary)
    ){
        Text(text=status.status,
            modifier=modifier,
            textAlign = TextAlign.Center,
            style=MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun CancelAcceptButtons(modifier: Modifier=Modifier, onCancel:()->Unit, onAccept:()->Unit, enabled:Boolean=true){
    Row(modifier=modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ){
        val cancelColor = when(enabled){
            true -> MaterialTheme.colorScheme.tertiary
            false -> MaterialTheme.colorScheme.onTertiary
        }
        val onCancelColor = when(enabled){
            true -> MaterialTheme.colorScheme.onTertiary
            false -> MaterialTheme.colorScheme.tertiary
        }
        Surface(){
            Button(onClick = onCancel,
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = cancelColor,
                    contentColor = onCancelColor
            )
            ){
                Text(text="Cancelar",
                    style=MaterialTheme.typography.headlineSmall)
            }
        }
        Button(onClick = onAccept){
            Text(text="Aceptar",
                style=MaterialTheme.typography.headlineSmall)
        }

    }
}

@Composable
fun CancelConfirmationDialog(modifier: Modifier=Modifier, onConfirm:()->Unit, onDismiss:()->Unit){
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(text = "Confirmar Cancelacion")
        },
        text = {
            Text(text = "¿Seguro que quieres cancelar la reserva?", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("SI")
            }
        } ,
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("NO")
            }
        }
    )
}

@Composable
fun ScanResultAlert(title:String, result:String, showDialog: Boolean, onDismiss: () -> Unit){
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(text = result,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
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


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ReservationSummaryWayPreview(){
    val viewModel: ReservationSummaryViewModel = ReservationSummaryViewModel(SavedStateHandle())
    AppTheme (darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ){
            ReservationSummary(modifier = Modifier.padding(5.dp), viewModel = viewModel)
        }
    }
}