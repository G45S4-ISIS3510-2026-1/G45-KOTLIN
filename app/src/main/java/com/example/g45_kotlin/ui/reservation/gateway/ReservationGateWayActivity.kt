package com.example.g45_kotlin.ui.reservation.gateway

import androidx.compose.foundation.gestures.animateScrollBy
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.g45_kotlin.ui.reservation.gateway.components.PaymentSelection
import com.example.g45_kotlin.ui.reservation.gateway.components.SessionSelection
import com.example.g45_kotlin.ui.reservation.gateway.components.TutorBanner
import com.example.g45_kotlin.ui.theme.G45KOTLINTheme
import kotlinx.coroutines.launch

class ReservationGateWayActivity {
    /*TODO*/
    //Implementar API/ViewModel//
    @Composable
    fun ReservationGateWay(modifier: Modifier = Modifier, viewModel: ReservationViewModel){
        val selectionState=viewModel.sessionSelection.collectAsState()
        val listState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        val currentPaymentType = selectionState.value.selectedPaymentType

        LaunchedEffect(currentPaymentType){
            if (currentPaymentType==PaymentType.CARD){
                scope.launch {
                    listState.animateScrollBy(1000f)
                }
            }
        }
        Column(modifier=modifier){
            Spacer(modifier=modifier.height(10.dp))
            Row(modifier=modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ){
                Button(onClick = { /*TODO*/ }, modifier=modifier.requiredSize(50.dp), shape= CircleShape) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "GoBack",
                        modifier = Modifier
                            .requiredSize(25.dp)
                            .fillMaxSize()
                    )
                }
                Text(text="Realizar Reserva",
                    modifier=modifier.fillMaxWidth(fraction = 0.75f),
                    style=MaterialTheme.typography.displaySmall
                )
            }
            Box(modifier=modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = modifier,
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item { TutorBanner(modifier = modifier.fillMaxWidth()) }
                    item {
                        StepLabel(
                            modifier = modifier,
                            step = 1,
                            label = "Selecciona el horario"
                        )
                    }
                    item { SessionSelection(modifier = modifier.fillMaxWidth(),
                        state=selectionState.value,
                        onDateSelection = {viewModel.selectDate(it)},
                        onHourSelection = {viewModel.selectHour(it)},
                        days=viewModel.getDates(),
                        hours=viewModel.getHours()

                    ) }
                    item { StepLabel(modifier = modifier, step = 2, label = "Método de Pago") }
                    item { PaymentSelection(modifier = modifier.fillMaxWidth(),
                        state=selectionState.value,
                        paymentMethods= viewModel.getPaymentMethods(),
                        onPaymentMethodSelection = {viewModel.selectPaymentMethod(it)},
                        onPaymentSelection = {viewModel.selectPaymentType(it)}
                        ) }
                }
                ConfirmationBanner(modifier = modifier.align(Alignment.BottomCenter), state = selectionState.value)
            }
        }
    }


    @Composable
    fun ConfirmationBanner(modifier: Modifier = Modifier, state: ReservationGatewayState){
        /*TODO*/
        var showDialog by remember { mutableStateOf(false) }
        Button(onClick = { /*TODO*/showDialog = true }, modifier=modifier){
            Text(text="Confirmation Banner",
                modifier=modifier,
                style=MaterialTheme.typography.headlineSmall
            )
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    // Called when the user taps outside or presses the back button
                    showDialog = false
                },
                title = {
                    Text(text = "Reserva \"exitosa\"")
                },
                text = {
                    Text(text = "Hiciste una reserva para las ${state.selectedHour} del ${state.selectedDate} mediante ${state.selectedPaymentType} ${if (state.selectedPaymentType == PaymentType.CARD) "con ${state.selectedPaymentMethod.number.takeLast(4)}" else ""}")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false // Dismiss the dialog on button click
                            // Perform the confirmed action here
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog = false
                        }
                    ) {
                        Text("Ah bueno")
                    }
                }
            )
        }


    }

    @Composable
    fun StepLabel(modifier: Modifier = Modifier, step:Int, label:String) {
        Row(modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically){
            Surface(modifier=modifier.requiredSize(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary

                ){
                Box(modifier= modifier
                    .requiredSize(30.dp)
                    .fillMaxSize(),
                    contentAlignment = Alignment.Center){
                    Text(text = "$step",
                        modifier = modifier.fillMaxSize(),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Text(text = label,
                modifier = modifier,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun ReservationGateWayPreview(){
        G45KOTLINTheme(darkTheme = false) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primaryContainer
            ){
                ReservationGateWay(modifier = Modifier.padding(5.dp), viewModel = ReservationViewModel())
            }
        }
    }
}