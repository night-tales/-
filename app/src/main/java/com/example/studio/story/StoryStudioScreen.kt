package com.example.studio.story

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryStudioScreen(
    onBack: () -> Unit,
    viewModel: StoryViewModel = hiltViewModel()
) {
    val title by viewModel.title.collectAsStateWithLifecycle()
    val storyText by viewModel.storyText.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = { Text("Story Studio ✨ AI", color = Color.White, fontWeight = FontWeight.Bold) },
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
        ) {
            Text("العنوان", color = Color(0xFFA6A6B3), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::updateTitle,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1B263B),
                    unfocusedContainerColor = Color(0xFF1B263B),
                    focusedBorderColor = Color(0xFF64D8FF),
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("القصة", color = Color(0xFFA6A6B3), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = storyText,
                onValueChange = viewModel::updateStory,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1B263B),
                    unfocusedContainerColor = Color(0xFF1B263B),
                    focusedBorderColor = Color(0xFF64D8FF),
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("الكلمات: ${viewModel.getWordCount()}", color = Color(0xFFA6A6B3), fontSize = 12.sp)
                Text("المدة التقريبية: ${viewModel.getDurationString()}", color = Color(0xFFA6A6B3), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AiActionButton("تحسين", Modifier.weight(1f))
                AiActionButton("اختصار", Modifier.weight(1f))
                AiActionButton("توسيع", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AiActionButton("إعادة كتابة", Modifier.weight(1f))
                AiActionButton("✨ AI", Modifier.weight(1f), isPrimary = true)
            }
        }
    }
}

@Composable
fun AiActionButton(text: String, modifier: Modifier = Modifier, isPrimary: Boolean = false) {
    Button(
        onClick = { /* TODO: Trigger AI Action on selected text */ },
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) Color(0xFF64D8FF) else Color(0xFF23324C),
            contentColor = if (isPrimary) Color(0xFF0B0B14) else Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
