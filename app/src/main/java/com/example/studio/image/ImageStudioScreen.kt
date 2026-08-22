package com.example.studio.image

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageStudioScreen(
    onBack: () -> Unit,
    viewModel: ImageViewModel = hiltViewModel()
) {
    val prompt by viewModel.prompt.collectAsStateWithLifecycle()
    val negativePrompt by viewModel.negativePrompt.collectAsStateWithLifecycle()
    val style by viewModel.style.collectAsStateWithLifecycle()
    val aspectRatio by viewModel.aspectRatio.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = { Text("Image Generation", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B0B14))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Prompt", color = Color(0xFFA6A6B3), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = prompt,
                onValueChange = viewModel::updatePrompt,
                modifier = Modifier.fillMaxWidth().height(120.dp),
                colors = getStudioTextFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Negative Prompt", color = Color(0xFFA6A6B3), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = negativePrompt,
                onValueChange = viewModel::updateNegativePrompt,
                modifier = Modifier.fillMaxWidth(),
                colors = getStudioTextFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Style", color = Color(0xFFA6A6B3), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = style,
                        onValueChange = viewModel::updateStyle,
                        colors = getStudioTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Aspect Ratio", color = Color(0xFFA6A6B3), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = aspectRatio,
                        onValueChange = viewModel::updateAspectRatio,
                        colors = getStudioTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* TODO: Trigger generation */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64D8FF), contentColor = Color(0xFF0B0B14)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Generate Image", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun getStudioTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedContainerColor = Color(0xFF1B263B),
    unfocusedContainerColor = Color(0xFF1B263B),
    focusedBorderColor = Color(0xFF64D8FF),
    unfocusedBorderColor = Color.Transparent
)
