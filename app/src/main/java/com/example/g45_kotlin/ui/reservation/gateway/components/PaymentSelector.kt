package com.example.g45_kotlin.ui.reservation.gateway.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.g45_kotlin.R
import com.example.g45_kotlin.ui.reservation.gateway.PaymentMethod
import com.example.g45_kotlin.ui.reservation.gateway.PaymentType
import com.example.g45_kotlin.ui.reservation.gateway.ReservationGatewayState

@Composable
fun PaymentSelection(modifier: Modifier = Modifier,
                     state: ReservationGatewayState,
                     paymentMethods:MutableSet<PaymentMethod>,
                     onPaymentSelection:(PaymentType) -> Unit,
                     onPaymentMethodSelection:(PaymentMethod) -> Unit){
    val currentPaymentType = state.selectedPaymentType

    Column(modifier=modifier){
        PaymentOption(modifier=modifier, type = PaymentType.RECEIPT, selected = currentPaymentType == PaymentType.RECEIPT, onClick = onPaymentSelection)
        Box(){

            Column(){
                AnimatedVisibility(
                    visible = currentPaymentType == PaymentType.CARD,
                    enter = expandVertically(expandFrom = Alignment.Top),
                    exit = shrinkVertically (shrinkTowards = Alignment.Top)
                ) {
                    PaymentMethodGroupSelector(modifier=Modifier, paymentMethods = paymentMethods, onSelection = onPaymentMethodSelection, state = state)
                }
            }
            PaymentOption(modifier=modifier, type = PaymentType.CARD, selected = currentPaymentType == PaymentType.CARD, onClick = onPaymentSelection)

        }
    }
    Spacer(modifier=modifier.height(100.dp))
}

@Composable
fun PaymentOption(modifier:Modifier, type: PaymentType, selected:Boolean, onClick: (PaymentType) -> Unit){
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val borderWidth = if (selected) 4.dp else 0.dp
    val label = when (type) {
        PaymentType.RECEIPT -> "Recibo Uniandes"
        PaymentType.CARD -> "Tarjeta Déb/Crédito"
    }
    val icono = when (type) {
        PaymentType.RECEIPT -> R.drawable.recibo_icon
        PaymentType.CARD ->R.drawable.tarjeta_icon
    }

    Surface(modifier=modifier
        .heightIn(min = 75.dp, max = 75.dp)
        .clickable { onClick(type) },
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(width = borderWidth, color = MaterialTheme.colorScheme.onPrimary),
        color = backgroundColor,
    ){
        Row(modifier=modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start){
            Icon(
                painter = painterResource(id = icono),
                contentDescription = "PaymentTypeIcon",
                tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.requiredSize(50.dp)
            )
            Spacer(modifier=Modifier.width(10.dp))
            Text(text=label, modifier=modifier,
                style=MaterialTheme.typography.headlineSmall
            )

        }
    }
}

@Composable
fun PaymentMethodGroupSelector(modifier: Modifier, paymentMethods: MutableSet<PaymentMethod>, onSelection: (PaymentMethod) -> Unit, state: ReservationGatewayState) {

    val currentMethod = state.selectedPaymentMethod
    Surface(modifier= Modifier, shape=RoundedCornerShape(50.dp), color = MaterialTheme.colorScheme.secondary){
        Column(Modifier.selectableGroup()) {
            Spacer(Modifier.requiredHeight(100.dp))
            paymentMethods.forEach { method ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (method.number == currentMethod.number),
                            onClick = { onSelection(method) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected =  method.number == currentMethod.number ,
                        onClick = {}
                    )
                    Text(
                        text = "************${method.number.takeLast(4)}",
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }

}