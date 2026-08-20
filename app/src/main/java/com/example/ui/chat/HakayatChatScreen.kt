package com.example.ui.chat

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GenerationStepInfo
import com.example.ui.components.GenerationTimeline
import com.example.ui.components.StepState


enum class ChatRole {
    USER,
    ASSISTANT
}

enum class ChatStatus {
    NORMAL,
    THINKING,
    STREAMING,
    ERROR,
    COMPLETE
}

data class ChatMessage(
    val id: String,
    val role: ChatRole,
    val text: String,
    val status: ChatStatus = ChatStatus.NORMAL
)

data class BlueprintUi(
    val title: String,
    val category: String,
    val duration: Int,
    val hero: String,
    val style: String,
    val format: String,
    val scenes: Int
)

data class CharacterReferenceUi(
    val id: String,
    val name: String,
    val role: String,
    val description: String,
    val style: String,
    val imageUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HakayatChatScreen(
    messages: List<ChatMessage>,
    blueprint: BlueprintUi? = null,
    characterReference: CharacterReferenceUi? = null,
    isGenerating: Boolean = false,
    generationProgress: Float = 0f,
    onBack: () -> Unit = {},
    onSend: (String) -> Unit = {},
    onStop: () -> Unit = {},
    onGenerate: () -> Unit = {},
    onEditBlueprint: () -> Unit = {},
    onRetry: () -> Unit = {},
    onNewChat: () -> Unit = {},
    onAttach: () -> Unit = {},
    onVoice: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var input by rememberSaveable { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var showQuickActions by remember { mutableStateOf(true) }
    var showExitDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    BackHandler(enabled = isGenerating) {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    onStop()
                    onBack()
                }) {
                    Text("خروج", color = Color(0xFFFF6B6B))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("إلغاء", color = Color.White)
                }
            },
            title = { Text("إيقاف الإنتاج؟", color = Color.White) },
            text = { Text("الخروج الآن سيؤدي إلى إيقاف عملية الإنتاج الحالية. هل أنت متأكد؟", color = Color(0xFFA6A6B3)) },
            containerColor = Color(0xFF1A1A2E)
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "حكايات الليل",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "مساعد الإنتاج الذكي",
                            color = Color(0xFFA6A6B3),
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            showMenu = true
                        }
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "القائمة",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B0B14)
                )
            )
        },
        bottomBar = {
            ChatInputBar(
                value = input,
                enabled = !isGenerating,
                onValueChange = {
                    input = it
                    showQuickActions = it.isBlank()
                },
                onSend = {
                    if (input.isNotBlank()) {
                        onSend(input.trim())
                        input = ""
                    }
                },
                onAttach = onAttach,
                onVoice = onVoice
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {

            if (messages.isEmpty()) {
                EmptyChat(
                    onQuickAction = {
                        input = it
                        showQuickActions = false
                    }
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                if (messages.isNotEmpty()) {
                    items(
                        items = messages,
                        key = { it.id }
                    ) { message ->
                        ChatMessageItem(message)
                    }
                }

                if (blueprint != null) {
                    item(key = "blueprint") {
                        BlueprintCard(
                            blueprint = blueprint,
                            onGenerate = onGenerate,
                            onEdit = onEditBlueprint
                        )
                    }
                }

                if (characterReference != null) {
                    item(key = "character") {
                        CharacterReferenceCard(character = characterReference)
                    }
                }

                if (isGenerating) {
                    item(key = "generation") {
                        GenerationCard(
                            progress = generationProgress,
                            onStop = onStop
                        )
                    }
                }

                item(key = "bottom-space") {
                    Spacer(Modifier.height(4.dp))
                }
            }

            AnimatedVisibility(
                visible = showQuickActions && messages.isNotEmpty() && !isGenerating,
                enter = fadeIn(tween(180)),
                exit = fadeOut(tween(120))
            ) {
                QuickActions(
                    onAction = {
                        input = it
                        showQuickActions = false
                    }
                )
            }
        }
    }

    if (showMenu) {
        ModalBottomSheet(
            onDismissRequest = {
                showMenu = false
            },
            sheetState = rememberModalBottomSheetState()
        ) {
            ChatMenu(
                onNewChat = {
                    showMenu = false
                    onNewChat()
                },
                onDismiss = {
                    showMenu = false
                }
            )
        }
    }
}

@Composable
private fun EmptyChat(
    onQuickAction: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Surface(
            modifier = Modifier.size(76.dp),
            shape = CircleShape,
            color = Color(0xFF16233D)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "☾",
                    fontSize = 42.sp
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "مساعد حكايات الليل",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "اكتب فكرتك، وسأحولها إلى قصة وصوت وصور وفيديو.",
            color = Color(0xFFA6A6B3),
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(28.dp))

        QuickActions(onAction = onQuickAction)
    }
}

@Composable
private fun QuickActions(
    onAction: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickAction(
            emoji = "✨",
            title = "قصة جديدة",
            subtitle = "ابدأ من فكرة بسيطة",
            onClick = {
                onAction("أريد إنشاء قصة جديدة")
            }
        )

        QuickAction(
            emoji = "🎬",
            title = "فيديو",
            subtitle = "قصة كاملة مع الصور والصوت",
            onClick = {
                onAction("أريد إنشاء فيديو قصة كامل")
            }
        )

        QuickAction(
            emoji = "🎨",
            title = "صورة",
            subtitle = "إنشاء مشهد أو شخصية",
            onClick = {
                onAction("أريد إنشاء صورة")
            }
        )
    }
}

@Composable
private fun QuickAction(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF121222)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = emoji,
                fontSize = 25.sp
            )

            Spacer(Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = subtitle,
                    color = Color(0xFFA6A6B3),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessage
) {
    val isUser = message.role == ChatRole.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        },
        verticalAlignment = Alignment.Top
    ) {

        if (!isUser) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = Color(0xFF16233D)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("☾", fontSize = 18.sp)
                }
            }

            Spacer(Modifier.width(8.dp))
        }

        Surface(
            modifier = Modifier
                .animateContentSize()
                .fillMaxWidth(
                    fraction = if (isUser) 0.82f else 0.90f
                ),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (isUser) 20.dp else 5.dp,
                bottomEnd = if (isUser) 5.dp else 20.dp
            ),
            color = if (isUser) {
                Color(0xFF233B5E)
            } else {
                Color(0xFF121222)
            }
        ) {

            Column(
                modifier = Modifier.padding(
                    horizontal = 15.dp,
                    vertical = 12.dp
                )
            ) {

                Text(
                    text = message.text,
                    color = Color.White,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                if (message.status == ChatStatus.THINKING) {
                    Spacer(Modifier.height(8.dp))
                    ThinkingDots()
                }

                if (message.status == ChatStatus.ERROR) {
                    Spacer(Modifier.height(10.dp))

                    TextButton(
                        onClick = {}
                    ) {
                        Text("إعادة المحاولة")
                    }
                }
            }
        }
    }
}

@Composable
private fun ThinkingDots() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        repeat(3) {
            Surface(
                modifier = Modifier.size(6.dp),
                shape = CircleShape,
                color = Color(0xFF64D8FF)
            ) {}
        }
    }
}

@Composable
private fun BlueprintCard(
    blueprint: BlueprintUi,
    onGenerate: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF121A2D)
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = "✨ التصور المقترح",
                color = Color(0xFF64D8FF),
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = blueprint.title,
                color = Color.White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoChip("🎬 ${blueprint.category}")
                InfoChip("⏱ ${blueprint.duration} د")
            }

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoChip("👤 ${blueprint.hero}")
                InfoChip("📱 ${blueprint.format}")
            }

            Spacer(Modifier.height(8.dp))

            InfoChip("🎨 ${blueprint.style}")

            Spacer(Modifier.height(8.dp))

            Text(
                text = "${blueprint.scenes} مشهد • راوي عربي",
                color = Color(0xFFA6A6B3),
                fontSize = 13.sp
            )

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OutlinedActionButton(
                    text = "✏ تعديل",
                    modifier = Modifier.weight(1f),
                    onClick = onEdit
                )

                FilledIconButton(
                    onClick = onGenerate,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = androidx.compose.material3.IconButtonDefaults
                        .filledIconButtonColors(
                            containerColor = Color(0xFF64D8FF),
                            contentColor = Color(0xFF0B0B14)
                        )
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        "ابدأ التوليد",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    text: String
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color(0xFF1A263D)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            color = Color(0xFFD8E7FF),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun OutlinedActionButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .border(
                width = 1.dp,
                color = Color(0xFF3C4A62),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun GenerationCard(
    progress: Float,
    onStop: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 350,
            easing = FastOutSlowInEasing
        ),
        label = "generationProgress"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF121222)
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = "🎬 إنتاج القصة",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            val steps = listOf(
                GenerationStepInfo("كتابة القصة", if (animatedProgress >= 0.14f) StepState.COMPLETED else if (animatedProgress > 0f) StepState.IN_PROGRESS else StepState.PENDING),
                GenerationStepInfo("تصميم الشخصيات", if (animatedProgress >= 0.28f) StepState.COMPLETED else if (animatedProgress > 0.14f) StepState.IN_PROGRESS else StepState.PENDING),
                GenerationStepInfo("إنشاء الصور", if (animatedProgress >= 0.5f) StepState.COMPLETED else if (animatedProgress > 0.28f) StepState.IN_PROGRESS else StepState.PENDING),
                GenerationStepInfo("توليد الصوت", if (animatedProgress >= 0.7f) StepState.COMPLETED else if (animatedProgress > 0.5f) StepState.IN_PROGRESS else StepState.PENDING),
                GenerationStepInfo("الموسيقى والمؤثرات", if (animatedProgress >= 0.85f) StepState.COMPLETED else if (animatedProgress > 0.7f) StepState.IN_PROGRESS else StepState.PENDING),
                GenerationStepInfo("المونتاج النهائي", if (animatedProgress >= 1f) StepState.COMPLETED else if (animatedProgress > 0.85f) StepState.IN_PROGRESS else StepState.PENDING)
            )

            GenerationTimeline(steps = steps)

            Spacer(Modifier.height(16.dp))

            androidx.compose.material3.LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF64D8FF),
                trackColor = Color(0xFF253047)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                color = Color(0xFFA6A6B3),
                fontSize = 12.sp
            )

            Spacer(Modifier.height(10.dp))

            TextButton(
                onClick = onStop,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    Icons.Default.Stop,
                    contentDescription = null
                )

                Spacer(Modifier.width(6.dp))

                Text("إيقاف")
            }
        }
    }
}

@Composable
private fun GenerationStep(
    icon: String,
    label: String,
    completed: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = icon,
            fontSize = 17.sp
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = label,
            color = if (completed) {
                Color(0xFF7BE495)
            } else {
                Color(0xFFA6A6B3)
            },
            modifier = Modifier.weight(1f),
            fontSize = 13.sp
        )

        if (completed) {
            Text(
                text = "✓",
                color = Color(0xFF7BE495)
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttach: () -> Unit,
    onVoice: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = Color(0xFF0F0F1B)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp,
                    vertical = 8.dp
                ),
            verticalAlignment = Alignment.Bottom
        ) {

            IconButton(
                onClick = onAttach,
                enabled = enabled
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "إضافة",
                    tint = Color(0xFFA6A6B3)
                )
            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                enabled = enabled,
                placeholder = {
                    Text(
                        "اكتب فكرة أو أمرًا...",
                        color = Color(0xFF777784)
                    )
                },
                shape = RoundedCornerShape(22.dp),
                maxLines = 5
            )

            Spacer(Modifier.width(4.dp))

            if (value.isBlank()) {
                IconButton(
                    onClick = onVoice,
                    enabled = enabled
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "صوت",
                        tint = Color(0xFF64D8FF)
                    )
                }
            } else {
                FilledIconButton(
                    onClick = onSend,
                    enabled = enabled,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "إرسال"
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMenu(
    onNewChat: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                bottom = 30.dp
            )
    ) {
        Text(
            text = "خيارات المحادثة",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(18.dp))

        TextButton(
            onClick = onNewChat,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "محادثة جديدة",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
        }

        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "إغلاق",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun CharacterReferenceCard(
    character: CharacterReferenceUi
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B263B)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "✨ هوية الشخصية (Reference Sheet)",
                color = Color(0xFFF9C74F),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFF23324C), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Character Portrait", tint = Color(0xFFF9C74F), modifier = Modifier.size(40.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = character.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(text = character.role, color = Color(0xFFF9C74F), fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(text = "الأسلوب: ${character.style}", color = Color(0xFFA6A6B3), fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(text = character.description, color = Color.White, fontSize = 14.sp, lineHeight = 20.sp)
            Spacer(Modifier.height(20.dp))
            OutlinedActionButton(
                text = "⬇️ تنزيل مرجع الشخصية (Download Reference)",
                modifier = Modifier.fillMaxWidth(),
                onClick = { /* Handle Download Action */ }
            )
        }
    }
}
