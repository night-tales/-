package com.example.studio.export

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun ExportScreen(
    onBack: () -> Unit,
    viewModel: ExportViewModel = hiltViewModel()
) {
    val quality by viewModel.quality.collectAsStateWithLifecycle()
    val fps by viewModel.fps.collectAsStateWithLifecycle()
    val aspectRatio by viewModel.aspectRatio.collectAsStateWithLifecycle()
    val isRendering by viewModel.isRendering.collectAsStateWithLifecycle()
    val renderProgress by viewModel.renderProgress.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = { Text("Export", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .padding(24.dp)
        ) {
            if (isRendering) {
                // Rendering UI
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Rendering", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))
                    LinearProgressIndicator(
                        progress = { renderProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = Color(0xFF64D8FF),
                        trackColor = Color(0xFF23324C)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${(renderProgress * 100).toInt()}%", color = Color(0xFFA6A6B3), fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Scene 7 / 9", color = Color(0xFFA6A6B3), fontSize = 14.sp)
                    Text("Encoding video...", color = Color(0xFF64D8FF), fontSize = 14.sp)
                }
            } else {
                // Export Options UI
                Text("الجودة", color = Color(0xFFA6A6B3), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OptionChip("720p", quality == "720p") { viewModel.updateQuality("720p") }
                    OptionChip("1080p", quality == "1080p") { viewModel.updateQuality("1080p") }
                    OptionChip("4K", quality == "4K") { viewModel.updateQuality("4K") }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("FPS", color = Color(0xFFA6A6B3), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OptionChip("30", fps == 30) { viewModel.updateFps(30) }
                    OptionChip("60", fps == 60) { viewModel.updateFps(60) }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Aspect Ratio", color = Color(0xFFA6A6B3), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                AspectRatioOption("16 : 9", "YouTube", aspectRatio == "16:9") { viewModel.updateAspectRatio("16:9") }
                Spacer(modifier = Modifier.height(8.dp))
                AspectRatioOption("9 : 16", "TikTok / Shorts / Reels", aspectRatio == "9:16") { viewModel.updateAspectRatio("9:16") }
                Spacer(modifier = Modifier.height(8.dp))
                AspectRatioOption("1 : 1", "Instagram", aspectRatio == "1:1") { viewModel.updateAspectRatio("1:1") }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = viewModel::startRender,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64D8FF), contentColor = Color(0xFF0B0B14)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Render Video", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun OptionChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0xFF64D8FF) else Color(0xFF23324C))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text, color = if (isSelected) Color(0xFF0B0B14) else Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AspectRatioOption(ratio: String, platform: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1B263B))
            .border(
                width = 2.dp,
                color = if (isSelected) Color(0xFF64D8FF) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(ratio, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.width(60.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(platform, color = Color(0xFFA6A6B3), fontSize = 14.sp, modifier = Modifier.weight(1f))
        if (isSelected) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF64D8FF))
        }
    }
}
