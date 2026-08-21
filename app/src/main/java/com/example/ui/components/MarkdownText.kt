package com.example.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    fontSize: TextUnit = 16.sp
) {
    val annotatedString = buildAnnotatedString {
        var currentIndex = 0
        val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
        
        val matches = boldRegex.findAll(text)
        for (match in matches) {
            val start = match.range.first
            val end = match.range.last + 1
            
            if (currentIndex < start) {
                append(text.substring(currentIndex, start))
            }
            
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            append(match.groupValues[1])
            pop()
            
            currentIndex = end
        }
        
        if (currentIndex < text.length) {
            append(text.substring(currentIndex))
        }
    }
    
    Text(
        text = annotatedString,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        lineHeight = fontSize * 1.5f
    )
}
