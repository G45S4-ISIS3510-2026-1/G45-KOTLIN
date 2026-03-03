package com.example.g45_kotlin.ui.reservation.gateway
import androidx.lifecycle.ViewModel
import com.example.g45_kotlin.utilities.getDaysOfCurrentWeek
import com.example.g45_kotlin.utilities.getWorkingHours
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

enum class PaymentType {
    RECEIPT,
    CARD
}


class ReservationViewModel:ViewModel() {
    val mockPayments:List<PaymentMethod> = listOf(
        PaymentMethod("Juan Perez", "123456789", "08/29", 345),
        PaymentMethod("Maria Rodriguez", "987654321", "09/25", 123),
        PaymentMethod("Pedro Gomez", "456789123", "10/24", 789),
    )
    private val _sessionSelection = MutableStateFlow(ReservationGatewayState())
    val sessionSelection: StateFlow<ReservationGatewayState> =_sessionSelection.asStateFlow()

    private lateinit var currentDate : LocalDate
    private lateinit var currentHour : String

    private lateinit var currentPaymentType: PaymentType

    private lateinit var currentPaymentMethod: PaymentMethod

    private var paymentMethods: MutableSet<PaymentMethod> = mutableSetOf()

    fun selectDate (date:LocalDate) {
        currentDate = date
        _sessionSelection.value = _sessionSelection.value.copy(selectedDate = date)
    }

    fun selectHour (hour:String) {
        currentHour = hour
        _sessionSelection.value = _sessionSelection.value.copy(selectedHour = hour)
    }

    fun getDates () : List<LocalDate> {
        return getDaysOfCurrentWeek()
    }

    fun getHours () : List <String> {
        /*TODO*/
        return getWorkingHours()
    }

    fun getPaymentMethods () : MutableSet<PaymentMethod> {
        /*TODO*/
        return paymentMethods
    }

    fun addPaymentMethod (method:PaymentMethod) {
        /*TODO*/
        paymentMethods.add(method)
    }

    fun selectPaymentType (type: PaymentType) {
        currentPaymentType = type
        _sessionSelection.value = _sessionSelection.value.copy(selectedPaymentType = type)
    }

    fun selectPaymentMethod (method: PaymentMethod){
        currentPaymentMethod = method
        _sessionSelection.value = _sessionSelection.value.copy(selectedPaymentMethod = method)
    }




    private fun reset (){
        _sessionSelection.value=ReservationGatewayState(selectedDate = LocalDate.now(),
            selectedHour = "7:00 am",
            selectedPaymentType = PaymentType.RECEIPT,
            selectedPaymentMethod = PaymentMethod()
        )
        paymentMethods.clear()
        mockPayments.forEach { paymentMethods.add(it) }
    }

    init {
        reset()
    }
}