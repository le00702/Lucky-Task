package com.example.luckytask.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlin.math.sqrt

/*** Add constructor taking context and onShake function --> extend SensorEventListener
 * --> to avoid it being a separate Activity ****/
class ShakeListener(
    context: Context,
    private val onShake: () -> Unit,
) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val TAG = "[SENSOR]"

    /*** The value of 15 was chosen based on experiments on a physical device
     *   --> emulator did not work for this. We started at 2, got to 5, 10, and then went
     *   up to 15. 15 provides a strong protection against recognizing random/slower movements
     *   as a "shaking motion" ***/
    private val threshold = 15

    fun start() {
        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        Log.d(TAG, "Registered sensor")
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
        Log.d(TAG, "Unregistered sensor")
    }

    /*** Calculate acceleration for shake detection --> used this StackOverflow post
     *   for help: https://stackoverflow.com/questions/79414515/detect-shake-on-android-in-jetpack-compose ****/
    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        /*** Removing the gravitational force of the earth is necessary as it influences
         *   the values --> around 9.81 for z (straight down, when laying flat) ***/
        val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
        /*Log.d(TAG, "x: $x, y: $y, z: $z")*/
        if (acceleration > threshold) {
            Log.d(TAG, "Acc: $acceleration, Thr: $threshold")
            onShake()
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        /*** Change later maybe --> TODOs result in crash ***/
        Log.d(TAG, "Accuracy of sensor ${sensor?.name} changed to $accuracy")
    }

}