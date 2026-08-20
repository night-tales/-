package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class StepState {
    PENDING, IN_PROGRESS, COMPLETED
}

data class GenerationStepInfo(
    val label: String,
    val state: StepState
)

@Composable
fun GenerationTimeline(
    steps: List<GenerationStepInfo>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        steps.forEachIndexed { index, step ->
            val isLast = index == steps.size - 1
            Row(modifier = Modifier.fillMaxWidth()) {
                // Step Indicator & Line
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(32.dp)
                ) {
                    val indicatorColor = when (step.state) {
                        StepState.COMPLETED -> Color(0xFF7BE495) // Success
                        StepState.IN_PROGRESS -> Color(0xFF64D8FF) // Primary
                        StepState.PENDING -> Color(0xFF3C4A62) // Inactive
                    }
                    
                    Box(
                        modifier = Modifier.size(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(color = indicatorColor)
                            if (step.state == StepState.IN_PROGRESS) {
                                drawCircle(color = Color.White, radius = size.minDimension / 4)
                            }
                        }
                    }
                    
                    if (!isLast) {
                        val lineColor = if (step.state == StepState.COMPLETED) {
                            Color(0xFF7BE495)
                        } else {
                            Color(0xFF3C4A62)
                        }
                        Canvas(modifier = Modifier.width(2.dp).height(32.dp)) {
                            drawLine(
                                color = lineColor,
                                start = Offset(size.width / 2, 0f),
                                end = Offset(size.width / 2, size.height),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Step Label
                val textColor = when (step.state) {
                    StepState.COMPLETED -> Color.White
                    StepState.IN_PROGRESS -> Color.White
                    StepState.PENDING -> Color(0xFFA6A6B3)
                }
                val fontWeight = if (step.state == StepState.IN_PROGRESS) FontWeight.Bold else FontWeight.Normal
                
                Text(
                    text = step.label,
                    color = textColor,
                    fontSize = 15.sp,
                    fontWeight = fontWeight,
                    modifier = Modifier.padding(top = (-2).dp)
                )
            }
        }
    }
}
