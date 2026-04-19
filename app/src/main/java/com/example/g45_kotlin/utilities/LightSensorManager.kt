package com.example.g45_kotlin.utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.HandlerThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class LightSensorManager(context: Context) {
    // Gestor de sensores para obtener listener del sensor de luz
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    // Thread handler para tomar lecturas del sensor de luz en segundo plano
    private var handlerThread: HandlerThread? = null
    private var sensorHandler: Handler? = null

    // StateFlow para mantener el valor del sensor de luz (como un estado de ViewModel, sirviendo
    // como notifier para la UI que sería el observer)
    private val _luxValue = MutableStateFlow(0f)
    val luxValue: StateFlow<Float> = _luxValue

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let { _luxValue.value = it.values[0] }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }
    fun deviceHasLightSensor(): Boolean {
        return lightSensor != null
    }

    fun start() {
        // Solo sirve si el celular tiene sensor de luz
        if (lightSensor == null) return
        //Inicia el thread del sensor
        handlerThread = HandlerThread("SensorThread").apply {
            start()
            sensorHandler = Handler(looper)
        }
        //Registra el listener para empezar a detectar cambios de luz
        sensorManager.registerListener(
            listener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL,
            sensorHandler
        )
    }

    fun stop() {
        //Borra el listener para dejar de recibir cambios de luz
        if (lightSensor == null) return
        sensorManager.unregisterListener(listener)
        //cierra el thread del sensor
        handlerThread?.quitSafely()
        handlerThread = null
        sensorHandler = null
    }
}