package com.example.luckytask.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

/*** Add constructor taking context and onShake function --> extend SensorEventListener
 * --> to avoid it being a separate Activity ****/
class ShakeListener(
    context: Context,
    onShake: () -> Unit,
) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    fun start() {
        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        Log.d("[SENSOR]", "Registered sensor")
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
        Log.d("[SENSOR]", "Unregistered sensor")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        /*** Change later --> TODOs result in crash ***/
        Log.d("[SENSOR]", "Sensor ${event?.sensor?.name} changed")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        /*** Change later maybe --> TODOs result in crash ***/
        Log.d("[SENSOR]", "Accuracy of sensor ${sensor?.name} changed to $accuracy")
    }

}