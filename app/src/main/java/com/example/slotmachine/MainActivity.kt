package com.example.slotmachine

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelCurrent = 0f
    private var accelLast = 0f
    private var shake = 0f

    private lateinit var viewModel: SlotMachineViewModel
    private var spinPlayer: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[SlotMachineViewModel::class.java]

        setContent {
            val resultMessage by viewModel.resultMessage

            SlotMachineApp(viewModel)

            LaunchedEffect(resultMessage) {
                if (resultMessage == "Ganhaste!") {
                    playWinSound()
                }
            }
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            accelLast = accelCurrent
            accelCurrent = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta = accelCurrent - accelLast
            shake = shake * 0.9f + delta

            if (shake > 12) {
                viewModel.spin()
                playSpinSound()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        spinPlayer?.release()
        spinPlayer = null
    }

    private fun playWinSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.win_sound)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
    }

    fun playSpinSound() {
        spinPlayer?.release()
        spinPlayer = MediaPlayer.create(this, R.raw.spin_sound)
        spinPlayer?.start()
        spinPlayer?.setOnCompletionListener {
            it.release()
            spinPlayer = null
        }
    }
}