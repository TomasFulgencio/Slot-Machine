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

    // Gerenciador de sensores para detectar movimento
    private lateinit var sensorManager: SensorManager

    // Variáveis para armazenar valores de aceleração
    private var accelCurrent = 0f
    private var accelLast = 0f
    private var shake = 0f

    // ViewModel da Slot Machine
    private lateinit var viewModel: SlotMachineViewModel

    // MediaPlayer usado para o som da spin
    private var spinPlayer: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o ViewModel
        viewModel = ViewModelProvider(this)[SlotMachineViewModel::class.java]

        // Define o conteúdo da interface usando Compose
        setContent {
            val resultMessage by viewModel.resultMessage    // Observa a mensagem do resultado do ViewModel

            SlotMachineApp(viewModel) // Chama o composable principal da app

            // Toca o som de vitória quando a mensagem "Ganhaste!" aparece
            LaunchedEffect(resultMessage) {
                if (resultMessage == "Ganhaste!") {
                    playWinSound()
                }
            }
        }

        // Inicializa e regista o sensor de movimento
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    // Metodo chamado sempre que há mudança no sensor
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            // Obtém os valores de aceleração nos eixos X, Y e Z
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Calcula a aceleração atual
            accelLast = accelCurrent
            accelCurrent = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta = accelCurrent - accelLast
            shake = shake * 0.9f + delta // Atualiza o valor do shake

            // Se o movimento for forte o suficiente, gira a slot machine
            if (shake > 12) {
                viewModel.spin()
                playSpinSound()
            }
        }
    }

    // Ignorado, mas necessário pelo SensorEventListener
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // Para de escutar o sensor quando a app é pausada
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // Libera recursos quando a activity é destruída
    override fun onDestroy() {
        super.onDestroy()
        spinPlayer?.release()
        spinPlayer = null
    }

    // Toca o som da vitória
    private fun playWinSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.win_sound)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release() // Libera o MediaPlayer após terminar
        }
    }

    // Toca o som do spin
    fun playSpinSound() {
        spinPlayer?.release() // Libera player anterior se existir
        spinPlayer = MediaPlayer.create(this, R.raw.spin_sound)
        spinPlayer?.start()
        spinPlayer?.setOnCompletionListener {
            it.release() // Libera o player após a reprodução
            spinPlayer = null
        }
    }
}