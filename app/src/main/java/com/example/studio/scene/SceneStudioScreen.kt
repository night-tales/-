package com.example.studio.scene

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneStudioScreen(
    onBack: () -> Unit,
    viewModel: SceneViewModel = hiltViewModel()
) {
    val scenes by viewModel.scenes.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = { Text("Storyboard", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B0B14))
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(scenes) { scene ->
                SceneCard(scene)
            }
        }
    }
}

@Composable
fun SceneCard(scene: com.example.domain.model.SceneInfo) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("SCENE ${String.format("%02d", scene.order)}", color = Color(0xFF64D8FF), fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        // Image Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1B263B)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFFA6A6B3), modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Details
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(scene.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                SceneProperty("الوصف:", scene.description)
                SceneProperty("المدة:", "00:${scene.durationSeconds}")
                SceneProperty("الكاميرا:", scene.cameraDirection)
                SceneProperty("الحركة:", scene.action)
                SceneProperty("الصوت:", scene.soundEnv)
                if (scene.dialog != null) {
                    SceneProperty("الحوار:", "\"${scene.dialog}\"")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23324C))) {
                Text("تغيير الكاميرا", color = Color.White, fontSize = 12.sp)
            }
            Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23324C))) {
                Text("إعادة الصورة", color = Color.White, fontSize = 12.sp)
            }
            Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64D8FF))) {
                Text("✨ تحسين", color = Color(0xFF0B0B14), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SceneProperty(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, color = Color(0xFFA6A6B3), fontSize = 14.sp, modifier = Modifier.width(80.dp))
        Text(value, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
    }
}
