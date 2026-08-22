import os

def write_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + '\n')

# 1. Voice Studio Screen
write_file('app/src/main/java/com/example/studio/voice/VoiceStudioScreen.kt', """
package com.example.studio.voice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceStudioScreen(onBack: () -> Unit) {
    var speed by remember { mutableFloatStateOf(1f) }
    var tone by remember { mutableFloatStateOf(0.5f) }
    var volume by remember { mutableFloatStateOf(0.8f) }
    var isMusicEnabled by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = { Text("Voice Studio", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Voice Selection (Mocked dropdown)
            Column {
                Text("الصوت:", color = Color(0xFFA6A6B3), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF23324C)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("عربي - صوت هادئ", fontSize = 16.sp)
                }
            }

            // Speed Slider
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("السرعة:", color = Color(0xFFA6A6B3), fontSize = 14.sp)
                    Text("${String.format("%.2fx", speed)}", color = Color.White, fontSize = 14.sp)
                }
                Slider(
                    value = speed,
                    onValueChange = { speed = it },
                    valueRange = 0.5f..2f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF64D8FF), activeTrackColor = Color(0xFF64D8FF), inactiveTrackColor = Color(0xFF23324C))
                )
            }

            // Tone Slider
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("هادئة", color = Color(0xFFA6A6B3), fontSize = 14.sp)
                    Text("حماسية", color = Color(0xFFA6A6B3), fontSize = 14.sp)
                }
                Slider(
                    value = tone,
                    onValueChange = { tone = it },
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF64D8FF), activeTrackColor = Color(0xFF64D8FF), inactiveTrackColor = Color(0xFF23324C))
                )
            }

            // Music Toggle & Volume
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("الموسيقى:", color = Color(0xFFA6A6B3), fontSize = 14.sp, modifier = Modifier.weight(1f))
                Switch(
                    checked = isMusicEnabled,
                    onCheckedChange = { isMusicEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF64D8FF))
                )
            }

            if (isMusicEnabled) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Volume:", color = Color(0xFFA6A6B3), fontSize = 14.sp)
                        Text("${(volume * 100).toInt()}%", color = Color.White, fontSize = 14.sp)
                    }
                    Slider(
                        value = volume,
                        onValueChange = { volume = it },
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF64D8FF), activeTrackColor = Color(0xFF64D8FF), inactiveTrackColor = Color(0xFF23324C))
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { /* TODO: Play Preview */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23324C), contentColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF64D8FF))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Preview", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
""")

# 2. Timeline Studio Screen
write_file('app/src/main/java/com/example/studio/timeline/TimelineScreen.kt', """
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
                TimelineSegment("", 1f, Color(0xFF9013FE))
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
                TimelineSegment("", 1f, Color.White.copy(alpha = 0.5f))
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
""")
