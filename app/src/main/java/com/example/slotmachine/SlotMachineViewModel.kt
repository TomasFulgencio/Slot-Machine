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

    fun spin() {
        if (_isSpinning.value) return

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
                _resultMessage.value = "Ganhaste!"
            }
        }
    }
}
