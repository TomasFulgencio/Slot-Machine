package com.example.slotmachine

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SlotMachineViewModel : ViewModel() {

    // Lista de símbolos disponíveis para os rolos da slot machine
    private val symbols = listOf("🍒", "🍋", "🔔", "🍉", "⭐", "7️⃣")

    // Estado dos rolos atuais
    private val _slots = mutableStateListOf("🍒", "🍒", "🍒")
    val slots: List<String> get() = _slots

    // Indica se a slot machine está a girar
    private val _isSpinning = mutableStateOf(false)
    val isSpinning: State<Boolean> get() = _isSpinning

    // Mensagem de resultado
    private val _resultMessage = mutableStateOf<String?>(null)
    val resultMessage: State<String?> get() = _resultMessage

    // Contador de vitórias do jogador
    private val _winCount = mutableStateOf(0)
    val winCount: State<Int> get() = _winCount

    // Número de moedas disponíveis para o jogador
    private val _coins = mutableStateOf(50)
    val coins: State<Int> get() = _coins

    fun spin() {
        if (_isSpinning.value || _coins.value <= 0) return // Se já estiver a girar ou não houver moedas, não faz nada

        _coins.value -= 1 // Gasta uma moeda ao iniciar um spin
        _isSpinning.value = true // Define o estado como "a girar"
        _resultMessage.value = null // limpa a mensagem do resultado anterior

        viewModelScope.launch {
            repeat(15) { // Gira os rolos 15 vezes com atraso entre cada spin
                _slots[0] = symbols.random()
                _slots[1] = symbols.random()
                _slots[2] = symbols.random()
                delay(70) // Aguarda 70ms entre cada atualização
            }
            _isSpinning.value = false // Termina o estado do spin

            // Verifica se os 3 símbolos são iguais
            if (_slots[0] == _slots[1] && _slots[1] == _slots[2]) {
                _winCount.value++ // Incrementa o número de vitórias
                _coins.value += 5 // Dá 5 moedas como prémio
                _resultMessage.value = "Ganhaste!" // Mostra mensagem de vitória
            }
        }
    }
}
