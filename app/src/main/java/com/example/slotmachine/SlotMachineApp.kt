package com.example.slotmachine

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SlotMachineApp(viewModel: SlotMachineViewModel = viewModel()) {
    // Observa os estados da ViewModel
    val slots = viewModel.slots
    val isSpinning by viewModel.isSpinning
    val resultMessage by viewModel.resultMessage
    val coins by viewModel.coins
    val winCount by viewModel.winCount

    val context = LocalContext.current

    // Cor de fundo a dourado se ganhar, preto caso contrário
    val backgroundColor by animateColorAsState(
        targetValue = if (resultMessage == "Ganhaste!") Color(0xFFFFD700) else Color.Black,
        animationSpec = tween(durationMillis = 500)
    )

    // Animação de piscar nos símbolos quando se ganha
    val transition = rememberInfiniteTransition()
    val blinkingAlpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Layout principal em coluna
    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupa toda a tela
            .background(backgroundColor) // Cor de fundo animada
            .padding(16.dp), // Espaçamento interno
        verticalArrangement = Arrangement.Center, // Centraliza verticalmente
        horizontalAlignment = Alignment.CenterHorizontally // Centraliza horizontalmente
    ) {

        // Exibe o número de moedas e vitórias
        Text(
            text = "Moedas: $coins | Vitórias: $winCount",
            fontSize = 18.sp,
            color = Color.Yellow,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Linha com os símbolos da slot machine
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            slots.forEach { symbol ->
                Text(
                    text = symbol,
                    fontSize = 48.sp,
                    color = Color.White,
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = if (resultMessage == "Ganhaste!") blinkingAlpha else 1f
                        } // Aplica efeito de piscar se ganhar
                        .background(Color.DarkGray, shape = RoundedCornerShape(8.dp)) // Fundo arredondado
                        .padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp)) // Espaço entre os símbolos e o botão

        // Botão para girar a slot machine
        Button(
            onClick = {
                // Toca o som e chama o metodo de spin na ViewModel
                (context as? MainActivity)?.playSpinSound()
                viewModel.spin()
            },
            enabled = !isSpinning, // Desabilita se estiver a girar
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSpinning) Color.Gray else Color.Red
            )
        ) {
            Text(
                if (isSpinning) "Spinning..." else "SPIN", // Texto muda dependendo do estado
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        // Mensagem de vitória, se existir
        resultMessage?.let { message ->
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = message,
                fontSize = 24.sp,
                color = Color.Yellow,
                fontWeight = FontWeight.Bold
            )
        }

        // Mensagem se não houver moedas
        if (coins <= 0 && !isSpinning) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Sem moedas!",
                color = Color.Red,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}