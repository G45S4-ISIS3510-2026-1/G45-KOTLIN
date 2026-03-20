package com.example.g45_kotlin.ui.reservation.summary

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g45_kotlin.data.auth.AuthHolder
import com.example.g45_kotlin.data.auth.AuthRepository
import com.example.g45_kotlin.data.reservation.ReservationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class ReservationSummaryViewModel  (
    private val savedStateHandle: SavedStateHandle
):ViewModel(){
    private val reservationRepository = ReservationRepository
    private val authRepository=AuthHolder.authRepo


    private val _summaryState = MutableStateFlow(ReservationSummaryState(isLoading = savedStateHandle["isLoading"] as? Boolean  ?: false))
    val summaryState: StateFlow<ReservationSummaryState> =_summaryState.asStateFlow()

    private val _scanResult = MutableStateFlow<Boolean?>(savedStateHandle["scanResult"] as Boolean?)
    val scanResult: StateFlow<Boolean?> =_scanResult.asStateFlow()

    val testingId="PD54rQeGzxAye0mcIuwd"
    val testingVerifCode="540CL3213123"

    private val sessionId:String=savedStateHandle["session_id"] as String? ?: testingId

    val isTutor= authRepository.getCurrentUser()?.uid==summaryState.value.tutor.id




    fun verifyScanCode(code:String){
        viewModelScope.launch(Dispatchers.IO){
            _summaryState.value=_summaryState.value.copy(isLoading = true)
            try{
                val response=reservationRepository.sessionConfirmation(testingId, code)
                _summaryState.value=_summaryState.value.copy(isLoading = false)
                Log.d("ReservationSummaryViewModel", "Session confirmation response: ${response.body()}")
                if (response.isSuccessful){
                    _summaryState.update{it.copy(status = Status.CONCLUDED)}
                    savedStateHandle["status"]=Status.CONCLUDED
                    _scanResult.value=true
                    savedStateHandle["scanResult"]=true
                    savedStateHandle["isLoading"]=false
                }
                else{
                    _scanResult.value=false
                    savedStateHandle["scanResult"]=false
                }
            }
            catch (e:Exception){
                _scanResult.value=false
            }
            _summaryState.value=_summaryState.value.copy(isLoading = false)
        }
    }

    fun cancelReservation(){
        viewModelScope.launch(Dispatchers.IO){
            _summaryState.value=_summaryState.value.copy(isLoading = true)
            try{
                val response=reservationRepository.cancelSession(sessionId)
                if (response.isSuccessful){
                    _summaryState.update{it.copy(status = Status.CANCELLED)}
                _summaryState.value=_summaryState.value.copy(isLoading = false)
                }
            }
                catch (e:Exception){
                _summaryState.value=_summaryState.value.copy(isLoading = false)
            }
        }
    }

    fun tutorSite():Boolean{
        return isTutor
    }

    fun fetchSessionData(savedStateHandle: SavedStateHandle=this.savedStateHandle){
        Log.d("ReservationSummaryViewModel", "Fetching session data...")
        viewModelScope.launch(Dispatchers.IO){
            _summaryState.update { it.copy(isLoading = true) }
            try {
                val response = reservationRepository.getSession(sessionId)
                Log.d("ReservationSummaryViewModel", "Session data response: ${response.body()}")
                if (response.isSuccessful) {
                    val session = response.body()
                    val parsedDate =try{
                        Instant.parse(session?.scheduledAt).atZone(ZoneId.systemDefault()).toLocalDateTime()}
                    catch (e:Exception){
                        Log.d("ReservationSummaryViewModel", "Error parsing date: ${e.message}")
                        LocalDateTime.now()
                    }
                    val student:UserData;
                    val tutor:UserData;
                    val studentQuery=reservationRepository.getParticipant(session?.studentId ?: "")
                    Log.d("ReservationSummaryViewModel", "Student data response: ${studentQuery.body()}")
                    if (studentQuery.isSuccessful){
                        val studentData=studentQuery.body()
                        student=UserData(
                            id = studentData?.id ?: "",
                            name = studentData?.name ?: "",
                            picture = studentData?.profileImageUrl ?: ""
                        )

                    }else{student=UserData()}
                    val tutorQuery=reservationRepository.getParticipant(session?.tutorId ?: "")
                    Log.d("ReservationSummaryViewModel", "Tutor data response: ${tutorQuery.body()}")
                    if (tutorQuery.isSuccessful){
                        val tutorData=tutorQuery.body()
                        tutor=UserData(
                            id = tutorData?.id ?: "",
                            name = tutorData?.name ?: "",
                            picture = tutorData?.profileImageUrl ?: "")
                    }else{tutor=UserData()}
                    _summaryState.update {
                        it.copy(
                            id = session?.id ?: testingId,
                            status = when (session?.status) {
                                "Pendiente" -> Status.PENDING
                                "Concluida" -> Status.CONCLUDED
                                "Cancelada" -> Status.CANCELLED
                                else -> Status.PENDING
                            }
,
                            date = parsedDate,
                            skill = session?.skill?.label ?: "",
                            qrContent = session?.verifCode ?: testingVerifCode,
                            isLoading = false,
                            tutor = tutor,
                            student = student
                        )
                    }
                }else{
                    _summaryState.value=_summaryState.value.copy(isLoading = false)
                }

                Log.d("ReservationSummaryViewModel", "Session data fetched. ${summaryState.value}")
            }
            catch (e:Exception){
                Log.d("ReservationSummaryViewModel", "Error fetching session data: ${e.message}")
                _summaryState.value=_summaryState.value.copy(isLoading = false)
            }
        }
        _scanResult.value=savedStateHandle["scanResult"] as Boolean?
        _summaryState.value=_summaryState.value.copy(isLoading = false)
        Log.d("ReservationSummaryViewModel", "Session data fetched. ${summaryState.value}")


    }

    init{
        fetchSessionData()
    }



}