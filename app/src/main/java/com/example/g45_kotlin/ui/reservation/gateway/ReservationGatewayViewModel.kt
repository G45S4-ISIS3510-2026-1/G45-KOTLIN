package com.example.g45_kotlin.ui.reservation.gateway
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g45_kotlin.data.reservation.ReservationRepository
import com.example.g45_kotlin.utilities.getDaysOfCurrentWeek
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

enum class PaymentType {
    RECEIPT,
    CARD
}


class ReservationGatewayViewModel():ViewModel(/*savedStateHandle*/ ) {
    private val reservationRep = ReservationRepository
    /*TODO*/
    //Fetch from API//
    val mockPayments:List<PaymentMethod> = listOf(
        PaymentMethod("Juan Perez", "123456789", "08/29", 345),
        PaymentMethod("Maria Rodriguez", "987654321", "09/25", 123),
        PaymentMethod("Pedro Gomez", "456789123", "10/24", 789),
    )
    /*TODO*/
    //Fetch from API//
    val mockUser: TutorUser = TutorUser()

    val tesTutorId="3kN2ROLVPylTO4d85dA3"
    val testStudentId="63yxDXGgQFpI1JgkpfZY"

    private val _sessionSelection = MutableStateFlow(ReservationGatewayState())
    val sessionSelection: StateFlow<ReservationGatewayState> =_sessionSelection.asStateFlow()

    private val availability:MutableMap<String, List<String>> = mutableMapOf()


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
        val weekday=currentDate.dayOfWeek.name
        return availability[weekday] ?: emptyList()
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

    fun formatHour(localDateTime:String): String {
        val instant= Instant.parse(localDateTime).atZone(ZoneId.systemDefault()).toLocalDateTime()
        return instant.toLocalTime().toString()
    }

    fun parsecheduling():String {
        val hour = currentHour
        val day = currentDate
        val date = LocalDateTime.of(
            day,
            Instant.parse(hour).atZone(ZoneId.systemDefault()).toLocalDateTime().toLocalTime()
        )
        return date.toString()
    }


    private fun fetchData (){
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        if (today.dayOfWeek.value in 1..6) {
            selectDate(today)
        }else{
            selectDate(tomorrow)
        }
        _sessionSelection.value = _sessionSelection.value.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val response=reservationRep.getParticipant(tesTutorId)
                if (response.isSuccessful){
                    val tutorData=response.body()
                    val tutor=TutorUser(
                        name = tutorData?.name ?: "",
                        skills = tutorData?.tutoringSkills?.toSet() ?: setOf(),
                        sessionPrice = tutorData?.sessionPrice ?: 0.0,
                        currentRating = 5.0,
                        picture = tutorData?.profileImageUrl ?: ""
                    )
                    _sessionSelection.value = _sessionSelection.value.copy(sessionTutor = tutor)
                    val tutorAvailability=tutorData?.availability
                    if (tutorAvailability != null) {
                        availability["MONDAY"] = tutorAvailability.monday.map { hour -> formatHour(hour) }
                        availability["TUESDAY"] = tutorAvailability.tuesday.map { hour -> formatHour(hour) }
                        availability["WEDNESDAY"] = tutorAvailability.wednesday.map { hour -> formatHour(hour) }
                        availability["THURSDAY"] = tutorAvailability.thursday.map { hour -> formatHour(hour) }
                        availability["FRIDAY"] = tutorAvailability.friday.map { hour -> formatHour(hour) }
                        availability["SATURDAY"] = tutorAvailability.saturday.map { hour -> formatHour(hour) }
                    }
                }
            }catch (e:Exception){
                Log.d("ReservationGatewayViewModel", "Error fetching tutor data: ${e.message}")
                _sessionSelection.value = _sessionSelection.value.copy(isLoading = false)
            }
        }
        _sessionSelection.value = _sessionSelection.value.copy(isLoading = false)
    }

    init {
        fetchData()
    }
}