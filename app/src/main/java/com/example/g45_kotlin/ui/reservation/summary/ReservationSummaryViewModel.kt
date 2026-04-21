package com.example.g45_kotlin.ui.reservation.summary

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g45_kotlin.data.auth.AuthHolder
import com.example.g45_kotlin.data.reservation.ReservationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.OffsetDateTime

class ReservationSummaryViewModel  (
    private val savedStateHandle: SavedStateHandle
):ViewModel(){
    private val reservationRepository = ReservationRepository
    private val authRepository=AuthHolder.authRepo

    private val currentUserId=authRepository.getCurrentUser()?.uid


    private val _summaryState = MutableStateFlow(ReservationSummaryState(isLoading = savedStateHandle["isLoading"] as? Boolean  ?: false))
    val summaryState: StateFlow<ReservationSummaryState> =_summaryState.asStateFlow()


    val testingId="PD54rQeGzxAye0mcIuwd"
    val testingVerifCode="540CL3213123"

    private val sessionId:String=savedStateHandle["session_id"] as String? ?: testingId

    private var isTutor= authRepository.getCurrentUser()?.uid==summaryState.value.tutor.id




    fun verifyScanCode(code:String){

        _summaryState.value=_summaryState.value.copy(isLoading = true, qrResult = null, qrTitleResult = null, fetchError = null)
        viewModelScope.launch(Dispatchers.IO){
            try{
                val response=reservationRepository.sessionConfirmation(sessionId, currentUserId!!, code)
                Log.d("ReservationSummaryViewModel", "Session confirmation response: ${response.getOrNull()}")
                withContext(Dispatchers.Main){
                    if (response.isSuccess){
                        _summaryState.update{it.copy(status = Status.CONCLUDED, qrResult = "Tu asistencia ha sido confirmada exitosamente", qrTitleResult = "Asistencia Confirmada")}
                    }
                    else{
                        _summaryState.update{it.copy(qrResult = "Tu asistencia no ha sido confirmada, revisa que el código sea el correcto", qrTitleResult = "Asistencia No Confirmada")}
                    }
                    _summaryState.value=_summaryState.value.copy(isLoading = false)
                }
            }
            catch (e:Exception){
                Log.d("ReservationSummaryViewModel", "Error verifying scan code: ${e.message}")
                withContext(Dispatchers.Main){
                _summaryState.update{it.copy(qrResult = "No pudimos verificar tu asistencia, por favor intentalo de nuevo.", qrTitleResult = "Asistencia No Confirmada")}
                }
            }
        }
    }

    fun cancelReservation(){
        _summaryState.value=_summaryState.value.copy(isLoading = true, fetchError = null)
        viewModelScope.launch(Dispatchers.IO){
            try{
                val response=reservationRepository.cancelSession(sessionId, authRepository.getCurrentUser()?.uid ?: "")
                withContext(Dispatchers.Main){
                    if (response.isSuccess){
                        _summaryState.update{it.copy(status = Status.CANCELLED, isLoading = false)}
                    }else{
                        _summaryState.update{it.copy(isLoading = false, fetchError = "No se pudo cancelar la reserva. Revisa tu conexion e intentalo mas tarde.")}
                    }
                }
            }
            catch (e:Exception){
                Log.d("ReservationSummaryViewModel", "Error canceling reservation: ${e.message}")
                withContext(Dispatchers.Main){
                    _summaryState.update{it.copy(isLoading = false, fetchError = "No se pudo cancelar la reserva. Revisa tu conexion e intentalo mas tarde.")}
                }
            }
        }
    }

    fun tutorSite():Boolean{
        return isTutor
    }

    fun clearResult(){
        _summaryState.value=_summaryState.value.copy(qrResult = null, qrTitleResult = null)
    }

    fun fetchSessionData(savedStateHandle: SavedStateHandle=this.savedStateHandle){
        Log.d("ReservationSummaryViewModel", "Fetching session data...")
        _summaryState.update { it.copy(isLoading = true, fetchError = null) }
        viewModelScope.launch(Dispatchers.IO){
            try {
                val result = reservationRepository.getSession(sessionId)
                Log.d("ReservationSummaryViewModel", "Session data response: ${result.getOrNull() ?: "Error"}")
                withContext(Dispatchers.Main){
                    if (result.isSuccess) {
                        val session = result.getOrThrow()
                        val parsedDate =try{
                            OffsetDateTime.parse(session.scheduledAt).toLocalDateTime()
                        }
                        catch (e:Exception){
                            Log.d("ReservationSummaryViewModel", "Error parsing date: ${e.message}")
                            LocalDateTime.now()
                        }
                        val student:UserData=UserData(id=session.student.id ?: "studentId", name = session.student.name ?: "studentName", picture = session.student.profileImageUrl ?: "studentPicture");
                        val tutor:UserData=UserData(id = session.tutor.id ?: "tutorId", name = session.tutor.name ?: "tutorName", picture = session.tutor.profileImageUrl ?: "tutorPicture")
                        isTutor= authRepository.getCurrentUser()?.uid==tutor.id
                        _summaryState.update {
                            it.copy(
                                id = session.id ?: testingId,
                                status = when (session.status) {
                                    "Pendiente" -> Status.PENDING
                                    "Concluida" -> Status.CONCLUDED
                                    "Cancelada" -> Status.CANCELLED
                                    "Vencida" -> Status.OVERDUE
                                    else -> Status.PENDING
                                }
                                ,
                                date = parsedDate,
                                skill = session.skill.label ?: "",
                                qrContent = session.verifCode ?: testingVerifCode,
                                isLoading = false,
                                tutor = tutor,
                                student = student
                            )
                        }
                    }else{
                        _summaryState.value=_summaryState.value.copy(isLoading = false, fetchError = "No se pudo cargar la información de la sesión. Revisa tu conexion e intentalo mas tarde.")
                    }
                }

                Log.d("ReservationSummaryViewModel", "Session data fetched. ${summaryState.value}")
            }
            catch (e:Exception){
                Log.d("ReservationSummaryViewModel", "Error fetching session data: ${e.message}")
                withContext(Dispatchers.Main){
                    _summaryState.value=_summaryState.value.copy(isLoading = false, fetchError = "No se pudo cargar la información de la sesión. Revisa tu conexion e intentalo mas tarde.")
                }
            }
        }
        Log.d("ReservationSummaryViewModel", "Session data fetched. ${summaryState.value}")
    }

    init{
        fetchSessionData()
    }



}