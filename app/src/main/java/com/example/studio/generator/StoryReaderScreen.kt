package com.example.studio.generator

import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private fun exportToPdf(context: Context, title: String, content: String) {
    val webView = WebView(context)
    val html = """
        <!DOCTYPE html>
        <html dir="rtl" lang="ar">
        <head>
        <meta charset="UTF-8">
        <style>
        body { font-family: sans-serif; padding: 40px; text-align: right; line-height: 1.8; color: #333; }
        h1 { color: #111; text-align: center; margin-bottom: 30px; }
        p { font-size: 18px; margin-bottom: 15px; }
        </style>
        </head>
        <body>
        <h1>$title</h1>
        <p>${content.replace("\n", "<br>")}</p>
        </body>
        </html>
    """.trimIndent()

    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val printAdapter = view.createPrintDocumentAdapter("Story_$title")
            val jobName = "Story_$title"
            printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
        }
    }
    webView.loadDataWithBaseURL(null, html, "text/HTML", "UTF-8", null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryReaderScreen(
    storyId: String,
    onBack: () -> Unit,
    viewModel: GeneratedStoryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var fontSize by remember { mutableStateOf(18f) }
    val currentStory by viewModel.currentStory.collectAsStateWithLifecycle()

    LaunchedEffect(storyId) {
        viewModel.loadStory(storyId)
    }

    Scaffold(
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = { Text(currentStory?.title ?: "جاري التحميل...", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    currentStory?.let { story ->
                        IconButton(onClick = { viewModel.toggleFavorite(story) }) {
                            Icon(
                                imageVector = if (story.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (story.isFavorite) Color.Red else Color.White
                            )
                        }
                        TextButton(onClick = { exportToPdf(context, story.title, story.content) }) {
                            Text("PDF", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TITLE, story.title)
                                putExtra(Intent.EXTRA_TEXT, "${story.title}\n\n${story.content}")
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }) {
                            Icon(Icons.Filled.Share, contentDescription = "Share", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B0B14))
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF1B263B),
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Font Size", fontSize = 16.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { if (fontSize > 12f) fontSize -= 2f },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23324C))
                        ) {
                            Text("A-", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${fontSize.toInt()}", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { if (fontSize < 32f) fontSize += 2f },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23324C))
                        ) {
                            Text("A+", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            currentStory?.let { story ->
                Text(
                    text = story.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (fontSize + 6f).sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = story.content,
                    color = Color(0xFFE0E0E0),
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * 1.5f).sp
                )
            } ?: run {
                CircularProgressIndicator(
                    color = Color(0xFF64D8FF),
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(32.dp)
                )
            }
        }
    }
}
