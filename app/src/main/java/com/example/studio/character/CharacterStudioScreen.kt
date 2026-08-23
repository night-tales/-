package com.example.studio.character

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
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
import com.example.data.local.entity.CharacterEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterStudioScreen(
    onBack: () -> Unit,
    viewModel: CharacterViewModel = hiltViewModel()
) {
    val characters by viewModel.characters.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddCharacterDialog(
            isGenerating = isGenerating,
            onDismiss = { showAddDialog = false },
            onGenerate = { prompt ->
                viewModel.generateCharacter(prompt)
                showAddDialog = false
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = { Text("استوديو الشخصيات", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (isGenerating) {
                        CircularProgressIndicator(color = Color(0xFF64D8FF), modifier = Modifier.size(24.dp).padding(end = 16.dp))
                    } else {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Character", tint = Color(0xFF64D8FF))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B0B14))
            )
        }
    ) { padding ->
        if (characters.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("لا توجد شخصيات بعد", color = Color.Gray, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64D8FF), contentColor = Color(0xFF0B0B14))
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("إنشاء شخصية بالذكاء الاصطناعي")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(characters, key = { it.id }) { char ->
                    CharacterGridCard(
                        char = char,
                        onDelete = { viewModel.deleteCharacter(char) }
                    )
                }
            }
        }
    }
}

@Composable
fun CharacterGridCard(
    char: CharacterEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(0.7f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (char.referenceImagePath != null) {
                AsyncImage(
                    model = char.referenceImagePath,
                    contentDescription = char.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC0B0B14)),
                                startY = 200f
                            )
                        )
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color(0xFF23324C)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFA6A6B3), modifier = Modifier.size(64.dp))
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(char.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(char.traits, color = Color(0xFF64D8FF), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(char.description, color = Color(0xFFE2E2E2), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(Color(0x88000000), RoundedCornerShape(50))
            ) {
                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun AddCharacterDialog(
    isGenerating: Boolean,
    onDismiss: () -> Unit,
    onGenerate: (String) -> Unit
) {
    var prompt by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = { if (!isGenerating) onDismiss() },
        containerColor = Color(0xFF1B263B),
        title = { Text("إنشاء شخصية جديدة", color = Color.White) },
        text = {
            Column {
                Text("صف الشخصية التي تريد إضافتها للقصة:", color = Color.LightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("مثال: فتى ذكي يرتدي نظارات ويحب المغامرة...") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0B0B14),
                        unfocusedContainerColor = Color(0xFF0B0B14),
                        focusedBorderColor = Color(0xFF64D8FF),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onGenerate(prompt) },
                enabled = prompt.isNotBlank() && !isGenerating,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64D8FF), contentColor = Color(0xFF0B0B14))
            ) {
                Text("توليد ✨", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isGenerating) {
                Text("إلغاء", color = Color.Gray)
            }
        }
    )
}
