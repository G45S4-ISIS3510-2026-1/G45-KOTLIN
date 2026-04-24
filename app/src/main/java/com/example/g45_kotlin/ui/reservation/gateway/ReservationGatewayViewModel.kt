package com.example.g45_kotlin.ui.reservation.gateway
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g45_kotlin.data.auth.AuthHolder
import com.example.g45_kotlin.data.reservation.ParticipantSummaryDto
import com.example.g45_kotlin.data.reservation.ReservationRepository
import com.example.g45_kotlin.data.reservation.SessionDto
import com.example.g45_kotlin.data.reservation.SkillSummaryDto
import com.example.g45_kotlin.utilities.AnalyticsManager
import com.example.g45_kotlin.utilities.GoogleAnalyticsService
import com.example.g45_kotlin.utilities.getDaysOfCurrentWeek
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

enum class PaymentType {
    RECEIPT,
    CARD
}


class ReservationGatewayViewModel(savedStateHandle: SavedStateHandle):ViewModel() {
    private val reservationRep = ReservationRepository
    /*TODO*/
    //Fetch from API//
    val mockPayments:List<PaymentMethod> = listOf(
        PaymentMethod("Juan Perez", "123456789", "08/29", 345),
        PaymentMethod("Maria Rodriguez", "987654321", "09/25", 123),
        PaymentMethod("Pedro Gomez", "456789123", "10/24", 789),
    )
    val testinSkill= SkillSummaryDto(
        id="iIOuogDeqyrMZZAcQLek",
        label = ""
    )

    private val _sessionSelection = MutableStateFlow(ReservationGatewayState())
    val sessionSelection: StateFlow<ReservationGatewayState> =_sessionSelection.asStateFlow()

    private val availability:MutableMap<String, List<String>> = mutableMapOf()
    private val skillsData:MutableList<SkillSummaryDto> = mutableListOf()


    private lateinit var currentDate : LocalDate
    private lateinit var currentHour : String

    private lateinit var currentPaymentType: PaymentType

    private lateinit var currentPaymentMethod: PaymentMethod

    private var currentSkill: SkillSummaryDto?=null


    private var paymentMethods: MutableSet<PaymentMethod> = mutableSetOf()

    fun selectDate (date:LocalDate) {
        currentDate = date
        currentHour=""
        _sessionSelection.value = _sessionSelection.value.copy(selectedDate = date, selectedHour = "")
    }

    fun selectHour (hour:String) {
        currentHour = hour
        _sessionSelection.value = _sessionSelection.value.copy(selectedHour = hour)
    }

    fun getSkillsData():List<SkillSummaryDto>{
        return skillsData
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

    fun selectSkill(skillId:String){
        val skill=skillsData.find { it.id==skillId }
        if (skill!=null){
            currentSkill=skill
            _sessionSelection.update { it.copy(selectedSkill = skill) }
        }else{
            Log.d("ReservationGatewayViewModel", "Skill not found")
        }
    }

    fun formatHour(localDateTime:String): String {
        val offSetInstant= OffsetDateTime.parse(localDateTime)
        val formatter= DateTimeFormatter.ofPattern("HH:mm")
        return offSetInstant.format(formatter)
    }

    fun parsecheduling():String {
        val hour = LocalTime.parse(currentHour)
        val day = currentDate
        val date = LocalDateTime.of(day, hour)
        return date.toString()
    }

    fun clearError(){
        _sessionSelection.value = _sessionSelection.value.copy(error = "")
    }

    val tutorId=savedStateHandle.get<String>("tutor_id") ?: "tesTutorId"
    val studentId=AuthHolder.authRepo.getCurrentUser()?.uid ?: "testStudentId"
    fun registerSession(onSuccess:(String)->Unit={}){
        if(this::currentHour.isInitialized && currentDate!=null ){
            if (currentHour!=""){
                val scheduling=parsecheduling()
                Log.d("ReservationGatewayViewModel", "Scheduling: $scheduling")
                _sessionSelection.value = _sessionSelection.value.copy(isLoading = true)
                viewModelScope.launch(Dispatchers.IO){
                    val session= SessionDto(
                        id = null,
                        student = ParticipantSummaryDto(id = studentId, name = "", profileImageUrl = ""),
                        tutor = ParticipantSummaryDto(id = tutorId, name = "", profileImageUrl = ""),
                        skill = currentSkill ?: testinSkill,
                        scheduledAt = scheduling,
                        status = "Pendiente",
                        verifCode = null
                    )
                    try{
                        val response=reservationRep.createSession(session)
                        if (response.isSuccessful){
                            GoogleAnalyticsService.logEvent(
                                "session_booked",
                                mapOf("tutor_id" to tutorId, "student_id" to studentId)
                            )
                            withContext(Dispatchers.Main){
                                onSuccess(response.body()?.id ?: "")
                            }
                            Log.d("ReservationGatewayViewModel", "Session created successfully, id=${response.body()?.id}")
                        }else{
                            Log.d("ReservationGatewayViewModel111", "Error creating session: ${response.errorBody()?.string()}")
                            _sessionSelection.value = _sessionSelection.value.copy(error = "Error creando la reserva, intenta denuevo: ${response.errorBody()?.toString()}")
                        }

                        _sessionSelection.value = _sessionSelection.value.copy(isLoading = false)
                    }catch (e:Exception){
                        AnalyticsManager.logError("RESERVATION_SERVICE", "Error creating session", e)
                        Log.d("ReservationGatewayViewModel222", "Error creating session: ${e.message}")
                        _sessionSelection.value = _sessionSelection.value.copy(isLoading = false)
                    }
                }
            }else{
                Log.d("ReservationGatewayViewModel", "No hour selected")
                _sessionSelection.value = _sessionSelection.value.copy(error = "Selecciona una hora válida")
            }
        }else if (currentSkill==null){
            Log.d("ReservationGatewayViewModel", "No date/hour selected")
            _sessionSelection.value = _sessionSelection.value.copy(error = "Selecciona una habilidad válida")
        }else{
            Log.d("ReservationGatewayViewModel", "No date/hour selected")
            _sessionSelection.value = _sessionSelection.value.copy(error = "Selecciona una fecha/hora válida")
        }
    }






    private fun fetchData (){
        _sessionSelection.value = _sessionSelection.value.copy(isLoading = true)
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        if (today.dayOfWeek.value in 1..5) {
            selectDate(tomorrow)
        }else{
            selectDate(tomorrow.plusDays(1))
        }
        skillsData.clear()
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val response=reservationRep.getParticipantData(tutorId)
                if (response.isSuccessful){
                    val tutorData=response.body()
                    val tutor=TutorUser(
                        name = tutorData?.name ?: "",
                        major = tutorData?.major ?: "",
                        skills = tutorData?.tutoringSkills?: emptyList(),
                        sessionPrice = tutorData?.sessionPrice ?: 0,
                        currentRating = tutorData?.tutorRating ?: 0.0,
                        picture = tutorData?.profileImageUrl ?: ""
                    )
                    _sessionSelection.value = _sessionSelection.value.copy(sessionTutor = tutor)
                    val tutorAvailability=tutorData?.availability
                    Log.d("ReservationGatewayViewModel", "Tutor availability: $tutorAvailability")
                    if (tutorAvailability != null) {
                        availability["MONDAY"] = tutorAvailability.monday.map { hour -> formatHour(hour) }
                        availability["TUESDAY"] = tutorAvailability.tuesday.map { hour -> formatHour(hour) }
                        availability["WEDNESDAY"] = tutorAvailability.wednesday.map { hour -> formatHour(hour) }
                        availability["THURSDAY"] = tutorAvailability.thursday.map { hour -> formatHour(hour) }
                        availability["FRIDAY"] = tutorAvailability.friday.map { hour -> formatHour(hour) }
                        availability["SATURDAY"] = tutorAvailability.saturday.map { hour -> formatHour(hour) }
                    }
                    val skillsResponse=reservationRep.getTutorSkills(tutorData?.tutoringSkills ?: emptyList())
                    if (skillsResponse.isSuccessful){
                        Log.d("ReservationGatewayViewModel", "Tutor skills: ${skillsResponse.body()}")
                        skillsData.addAll(skillsResponse.body() ?: emptyList())
                        _sessionSelection.value = _sessionSelection.value.copy(tutorSkills = skillsResponse.body() ?: emptyList())
                    }

                }
            }catch (e:Exception){
                Log.d("ReservationGatewayViewModel", "Error fetching tutor data: ${e.message}")
            }
            _sessionSelection.value = _sessionSelection.value.copy(isLoading = false)
        }
    }

    init {
        fetchData()
        paymentMethods.clear()
        mockPayments.forEach { addPaymentMethod(it) }
    }
}