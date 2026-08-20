package com.example.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape

data class Scene(val id: Int, val title: String, var text: String, var imagePrompt: String = "")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneEditorScreen(onBack: () -> Unit) {
    var scenes by remember { mutableStateOf(listOf(
        Scene(1, "المشهد الأول", "في ليلة هادئة، كانت الرياح تعصف بشدة...", "صورة ليلية عاصفة لقلعة قديمة"),
        Scene(2, "المشهد الثاني", "فجأة، ظهر ضوء غامض من بعيد...", "ضوء متوهج في غابة مظلمة"),
        Scene(3, "المشهد الثالث", "اقترب البطل بحذر ليرى ما يختبئ في الظلام...", "شخص يقف أمام كهف مضاء")
    )) }

    var editingScene by remember { mutableStateOf<Scene?>(null) }
    var editText by remember { mutableStateOf("") }
    
    var refiningImage by remember { mutableStateOf<Scene?>(null) }
    var editImagePrompt by remember { mutableStateOf("") }

    if (editingScene != null) {
        AlertDialog(
            onDismissRequest = { editingScene = null },
            confirmButton = {
                TextButton(onClick = {
                    scenes = scenes.map { if (it.id == editingScene!!.id) it.copy(text = editText) else it }
                    editingScene = null
                }) {
                    Text("حفظ", color = Color(0xFF64D8FF))
                }
            },
            dismissButton = {
                TextButton(onClick = { editingScene = null }) {
                    Text("إلغاء", color = Color.White)
                }
            },
            title = { Text("تعديل المشهد", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF1A1A2E),
                        unfocusedContainerColor = Color(0xFF1A1A2E),
                        focusedBorderColor = Color(0xFF64D8FF),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            containerColor = Color(0xFF121222)
        )
    }

    if (refiningImage != null) {
        AlertDialog(
            onDismissRequest = { refiningImage = null },
            confirmButton = {
                TextButton(onClick = {
                    scenes = scenes.map { if (it.id == refiningImage!!.id) it.copy(imagePrompt = editImagePrompt) else it }
                    refiningImage = null
                }) {
                    Text("إعادة توليد", color = Color(0xFFF9C74F))
                }
            },
            dismissButton = {
                TextButton(onClick = { refiningImage = null }) {
                    Text("إلغاء", color = Color.White)
                }
            },
            title = { Text("تحسين صورة المشهد", color = Color.White) },
            text = {
                Column {
                    Text("سيتم الحفاظ على هوية الشخصيات الأساسية. قم بوصف الخلفية أو الحركة الجديدة:", color = Color(0xFFA6A6B3), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editImagePrompt,
                        onValueChange = { editImagePrompt = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF1B263B),
                            unfocusedContainerColor = Color(0xFF1B263B),
                            focusedBorderColor = Color(0xFFF9C74F),
                            unfocusedBorderColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            containerColor = Color(0xFF1B263B)
        )
    }

    Scaffold(
        containerColor = Color(0xFF0D1B2A),
        topBar = {
            TopAppBar(
                title = { Text("محرر المشاهد", color = Color(0xFFF9C74F)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B2A))
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(scenes) { scene ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(scene.title, color = Color(0xFFF9C74F), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(Color(0xFF23324C), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Image, contentDescription = "Scene Image", tint = Color(0xFFA6A6B3), modifier = Modifier.size(48.dp))
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(scene.text, color = Color.White, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = {
                                refiningImage = scene
                                editImagePrompt = scene.imagePrompt
                            }) {
                                Icon(Icons.Default.ImageSearch, contentDescription = "Refine Image", modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(Modifier.width(4.dp))
                                Text("تعديل الصورة", color = Color.White)
                            }
                            TextButton(onClick = {
                                editingScene = scene
                                editText = scene.text
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(Modifier.width(4.dp))
                                Text("تعديل النص", color = Color.White)
                            }
                            TextButton(onClick = { /* Handle re-generate */ }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Regenerate", modifier = Modifier.size(16.dp), tint = Color(0xFFF9C74F))
                                Spacer(Modifier.width(4.dp))
                                Text("إعادة توليد", color = Color(0xFFF9C74F))
                            }
                        }
                    }
                }
            }
        }
    }
}
