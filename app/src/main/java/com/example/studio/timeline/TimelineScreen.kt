package com.example.studio.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.domain.audio.AudioPlayer
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    onBack: () -> Unit,
    viewModel: TimelineViewModel = hiltViewModel()
) {
    val scenes by viewModel.scenes.collectAsStateWithLifecycle()
    
    var currentSceneIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    
    val audioPlayer = remember { AudioPlayer() }

    // Playback Controller
    LaunchedEffect(currentSceneIndex, isPlaying) {
        if (isPlaying && scenes.isNotEmpty()) {
            val scene = scenes.getOrNull(currentSceneIndex)
            if (scene?.audioUrl != null) {
                audioPlayer.play(scene.audioUrl) {
                    // On Complete, go next
                    if (currentSceneIndex < scenes.size - 1) {
                        currentSceneIndex++
                    } else {
                        isPlaying = false
                    }
                }
            } else {
                // No audio, just wait 3 seconds and go next
                delay(3000)
                if (currentSceneIndex < scenes.size - 1) {
                    currentSceneIndex++
                } else {
                    isPlaying = false
                }
            }
        } else {
            audioPlayer.stop()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.stop()
        }
    }

    Scaffold(
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = { Text("المعاينة الزمنية (Timeline)", color = Color.White, fontWeight = FontWeight.Bold) },
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
        ) {
            // Player View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                val currentScene = scenes.getOrNull(currentSceneIndex)
                if (currentScene != null && currentScene.imageUrl != null) {
                    AsyncImage(
                        model = currentScene.imageUrl,
                        contentDescription = "Scene Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text("لا توجد صورة لهذا المشهد", color = Color.Gray)
                }
                
                // Subtitles Overlay
                if (currentScene != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                            .background(Color(0x88000000), RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = currentScene.narration,
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1B263B))
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (currentSceneIndex > 0) {
                        currentSceneIndex--
                    }
                }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.White)
                }
                
                FloatingActionButton(
                    onClick = { isPlaying = !isPlaying },
                    containerColor = Color(0xFF64D8FF),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, 
                        contentDescription = "Play/Pause"
                    )
                }
                
                IconButton(onClick = {
                    if (currentSceneIndex < scenes.size - 1) {
                        currentSceneIndex++
                    }
                }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White)
                }
            }

            // Timeline Tracks UI
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
            ) {
                Text("المشاهد", color = Color(0xFFA6A6B3), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(scenes) { index, scene ->
                        val isSelected = index == currentSceneIndex
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF64D8FF) else Color(0xFF23324C))
                                .padding(2.dp)
                        ) {
                            if (scene.imageUrl != null) {
                                AsyncImage(
                                    model = scene.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(6.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("الصوتيات", color = Color(0xFFA6A6B3), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(scenes) { index, scene ->
                        val hasAudio = scene.audioUrl != null
                        val isSelected = index == currentSceneIndex
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF9013FE) else if (hasAudio) Color(0xFF533483) else Color(0xFF23324C)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (hasAudio) {
                                Text("صوت", color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
