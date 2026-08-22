package com.example.studio.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = { Text("Timeline", color = Color.White, fontWeight = FontWeight.Bold) },
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
            // Mock Timeline Ruler
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("00:00", color = Color(0xFFA6A6B3), fontSize = 10.sp)
                Text("01:00", color = Color(0xFFA6A6B3), fontSize = 10.sp)
                Text("02:00", color = Color(0xFFA6A6B3), fontSize = 10.sp)
            }

            // Video Track
            TimelineTrack("VIDEO") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    TimelineSegment("Scene 1", 0.3f, Color(0xFF4A90E2))
                    TimelineSegment("Scene 2", 0.4f, Color(0xFF50E3C2))
                    TimelineSegment("Scene 3", 0.3f, Color(0xFFF5A623))
                }
            }

            // Voice Track
            TimelineTrack("VOICE") {
                Row(modifier = Modifier.fillMaxWidth()) { TimelineSegment("", 1f, Color(0xFF9013FE)) }
            }

            // Music Track
            TimelineTrack("MUSIC") {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(0.1f))
                    TimelineSegment("", 0.8f, Color(0xFFB8E986))
                    Spacer(modifier = Modifier.weight(0.1f))
                }
            }

            // Subtitle Track
            TimelineTrack("SUBTITLE") {
                Row(modifier = Modifier.fillMaxWidth()) { TimelineSegment("", 1f, Color.White.copy(alpha = 0.5f)) }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Tool buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimelineToolButton("Trim")
                TimelineToolButton("Split")
                TimelineToolButton("Delete")
                TimelineToolButton("Fade")
            }
        }
    }
}

@Composable
fun TimelineTrack(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(title, color = Color(0xFFA6A6B3), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF1B263B))
        ) {
            content()
        }
    }
}

@Composable
fun RowScope.TimelineSegment(label: String, weight: Float, color: Color) {
    Box(
        modifier = Modifier
            .weight(weight)
            .fillMaxHeight()
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        if (label.isNotEmpty()) {
            Text(label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TimelineToolButton(label: String) {
    TextButton(onClick = { /*TODO*/ }) {
        Text(label, color = Color(0xFF64D8FF), fontSize = 12.sp)
    }
}
