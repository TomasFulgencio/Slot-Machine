package com.example.slotmachine

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SlotMachineViewModel : ViewModel() {

    private val symbols = listOf("🍒", "🍋", "🔔", "🍉", "⭐", "7️⃣")

    private val _slots = mutableStateListOf("🍒", "🍒", "🍒")
    val slots: List<String> get() = _slots

    private val _isSpinning = mutableStateOf(false)
    val isSpinning: State<Boolean> get() = _isSpinning

    private val _resultMessage = mutableStateOf<String?>(null)
    val resultMessage: State<String?> get() = _resultMessage

    private val _winCount = mutableStateOf(0)
    val winCount: State<Int> get() = _winCount

    private val _coins = mutableStateOf(50)
    val coins: State<Int> get() = _coins

    fun spin() {
        if (_isSpinning.value || _coins.value <= 0) return

        _coins.value -= 1
        _isSpinning.value = true
        _resultMessage.value = null // limpa mensagem anterior

        viewModelScope.launch {
            repeat(15) {
                _slots[0] = symbols.random()
                _slots[1] = symbols.random()
                _slots[2] = symbols.random()
                delay(70)
            }
            _isSpinning.value = false

            // Verifica se os 3 são iguais
            if (_slots[0] == _slots[1] && _slots[1] == _slots[2]) {
                _winCount.value++
                _coins.value += 5
                _resultMessage.value = "Ganhaste!"
            }
        }
    }
}
