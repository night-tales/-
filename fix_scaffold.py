import re

with open('app/src/main/java/com/example/ui/editor/SceneEditorScreen.kt', 'r') as f:
    content = f.read()

start_str = "    Scaffold("
# We want to replace from Scaffold to the end of the file.

replacement = """    Scaffold(
        containerColor = Color(0xFF0D1B2A),
        topBar = {
            TopAppBar(
                title = { Text(if (isPreviewMode) "معاينة القصة" else "محرر المشاهد", color = Color(0xFFF9C74F)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { isPreviewMode = !isPreviewMode }) {
                        Icon(
                            if (isPreviewMode) Icons.Default.Edit else Icons.Default.Visibility,
                            contentDescription = "Toggle Mode",
                            tint = Color.White
                        )
                    }
                    if (isPreviewMode) {
                        IconButton(onClick = {
                            val fullStory = scenes.joinToString(". ") { it.narration }
                            tts?.speak(fullStory, TextToSpeech.QUEUE_FLUSH, null, "story_read")
                        }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play Aloud", tint = Color.White)
                        }
                        IconButton(onClick = {
                            val fullStory = scenes.joinToString("\n\n") { "## ${it.title}\n\n${it.narration}" }
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, fullStory)
                            }
                            context.startActivity(Intent.createChooser(intent, "مشاركة القصة"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B2A))
            )
        }
    ) { padding ->
        if (isPreviewMode) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(scenes) { scene ->
                    Column {
                        Text(scene.title, color = Color(0xFFF9C74F), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        MarkdownText(text = scene.narration, color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        } else {
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
                            
                            if (scene.imageUrl != null) {
                                coil.compose.AsyncImage(
                                    model = scene.imageUrl,
                                    contentDescription = "Scene Image",
                                    modifier = Modifier.fillMaxWidth().height(160.dp),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .background(Color(0xFF23324C), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Image, contentDescription = "Scene Image", tint = Color(0xFFA6A6B3), modifier = Modifier.size(48.dp))
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(scene.narration, color = Color.White, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = {
                                    refiningImage = scene
                                    editImagePrompt = scene.imagePrompt ?: ""
                                }) {
                                    Icon(Icons.Default.ImageSearch, contentDescription = "Refine Image", modifier = Modifier.size(16.dp), tint = Color.White)
                                    Spacer(Modifier.width(4.dp))
                                    Text("تعديل الصورة", color = Color.White)
                                }
                                TextButton(onClick = {
                                    editingScene = scene
                                    editText = scene.narration
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
}
"""

start_idx = content.find(start_str)

if start_idx != -1:
    new_content = content[:start_idx] + replacement
    with open('app/src/main/java/com/example/ui/editor/SceneEditorScreen.kt', 'w') as f:
        f.write(new_content)
