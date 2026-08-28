package com.example.studio.scene

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.local.entity.SceneEntity
import com.example.domain.audio.AudioPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneStudioScreen(
    onBack: () -> Unit,
    viewModel: SceneViewModel = hiltViewModel()
) {
    val scenes by viewModel.scenes.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    
    val audioPlayer = remember { AudioPlayer() }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.stop()
        }
    }

    Scaffold(
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = { Text("استوديو المشاهد", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (isGenerating) {
                        CircularProgressIndicator(color = Color(0xFF64D8FF), modifier = Modifier.size(24.dp).padding(end = 16.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B0B14))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::addNewScene,
                containerColor = Color(0xFF64D8FF)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Scene")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(scenes, key = { it.id }) { scene ->
                SceneCard(
                    scene = scene,
                    onGenerateImage = { viewModel.generateImage(scene.id) },
                    onAddVoiceOver = { viewModel.generateVoiceOver(scene.id) },
                    onEdit = { /* TODO */ },
                    isGenerating = isGenerating,
                    audioPlayer = audioPlayer
                )
            }
        }
    }
}

@Composable
fun SceneCard(
    scene: SceneEntity,
    onGenerateImage: () -> Unit,
    onAddVoiceOver: () -> Unit,
    onEdit: () -> Unit,
    isGenerating: Boolean,
    audioPlayer: AudioPlayer
) {
    var isPlaying by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "المشهد ${scene.index + 1}: ${scene.title}",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Narration Text
            Text(
                text = scene.narration,
                color = Color.LightGray,
                fontSize = 16.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Image Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0B0B14)),
                contentAlignment = Alignment.Center
            ) {
                if (scene.imageUrl != null) {
                    AsyncImage(
                        model = scene.imageUrl,
                        contentDescription = "صورة المشهد",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("لا توجد صورة", color = Color.Gray)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Audio Preview if exists
            if (scene.audioUrl != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0B0B14))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (isPlaying) {
                            audioPlayer.stop()
                            isPlaying = false
                        } else {
                            isPlaying = true
                            audioPlayer.play(scene.audioUrl) {
                                isPlaying = false
                            }
                        }
                    }) {
                        Icon(
                            if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = "Play/Stop",
                            tint = Color(0xFF64D8FF)
                        )
                    }
                    Text("الصوت مسجل", color = Color.White, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onGenerateImage,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23324C)),
                    enabled = !isGenerating
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF64D8FF))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("صورة", color = Color.White)
                }
                
                Button(
                    onClick = onAddVoiceOver,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF533483)),
                    enabled = !isGenerating
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("صوت", color = Color.White)
                }
            }
        }
    }
}
