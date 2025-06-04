package com.example.slotmachine

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SlotMachineApp(viewModel: SlotMachineViewModel = viewModel()) {
    val slots = viewModel.slots
    val isSpinning by viewModel.isSpinning
    val resultMessage by viewModel.resultMessage

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            slots.forEach { symbol ->
                Text(
                    text = symbol,
                    fontSize = 48.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                (context as? MainActivity)?.playSpinSound()
                viewModel.spin()
            },
            enabled = !isSpinning,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSpinning) Color.Gray else Color.Red
            )
        ) {
            Text(
                if (isSpinning) "Spinning..." else "SPIN",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        // Mensagem de vitória (se existir)
        resultMessage?.let { message ->
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = message,
                fontSize = 24.sp,
                color = Color.Yellow,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
