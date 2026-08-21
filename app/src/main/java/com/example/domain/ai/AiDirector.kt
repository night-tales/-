package com.example.domain.ai

import com.example.BuildConfig
import com.example.data.remote.gemini.Content
import com.example.data.remote.gemini.GenerateContentRequest
import com.example.data.remote.gemini.GenerateContentResponse
import com.example.data.remote.gemini.GenerationConfig
import com.example.data.remote.gemini.GeminiApiService
import com.example.data.remote.gemini.Part
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

class AiDirector @Inject constructor(
    private val geminiApi: GeminiApiService
) {
    private suspend fun generate(request: GenerateContentRequest): String = withContext(Dispatchers.IO) {
        val response = geminiApi.generateContent(BuildConfig.GEMINI_API_KEY, request)
        if (!response.isSuccessful) {
            val detail = response.errorBody()?.string()?.take(500).orEmpty()
            throw IllegalStateException("Gemini request failed (${response.code()}): $detail")
        }
        response.body()?.extractText()
            ?: throw IllegalStateException("Gemini returned an empty response")
    }

    private fun GenerateContentResponse.extractText(): String =
        candidates?.asSequence()
            ?.mapNotNull { candidate ->
                candidate.content?.parts?.asSequence()?.mapNotNull { it.text.trim() }?.firstOrNull()
            }
            ?.firstOrNull { it.isNotBlank() }
            ?: ""

    suspend fun understandIntent(userMessage: String): AiAction = runCatching {
        val prompt = """
            You are the AI Director for Night Tales Studio.
            Analyze the user's message and return ONLY one exact intent token:
            CREATE_STORY, MODIFY_STORY, CREATE_IMAGE, CREATE_AUDIO, CREATE_VIDEO, UNKNOWN.

            User message: $userMessage
        """.trimIndent()
        val text = generate(
            GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(temperature = 0f)
            )
        ).uppercase()

        when {
            text.contains("CREATE_STORY") -> AiAction.CreateStory(userMessage)
            text.contains("MODIFY_STORY") -> AiAction.ModifyStory(userMessage)
            text.contains("CREATE_IMAGE") -> AiAction.CreateImage(userMessage)
            text.contains("CREATE_AUDIO") -> AiAction.CreateAudio(userMessage)
            text.contains("CREATE_VIDEO") -> AiAction.CreateVideo("pending")
            else -> AiAction.Unknown
        }
    }.getOrElse { AiAction.Unknown }

    suspend fun generateChatReply(userMessage: String): String = runCatching {
        generate(
            GenerateContentRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(
                                text = """
                                    أنت المخرج الذكي في استوديو «حكايات الليل» (Night Tales Studio).
                                    ساعد المستخدم في تحويل فكرته إلى فيلم قصصي.
                                    أجب بالعربية الفصحى المبسطة، باختصار وتشجيع.
                                    رسالة المستخدم: $userMessage
                                """.trimIndent()
                            )
                        )
                    )
                )
            )
        )
    }.getOrElse { "عذراً، تعذر الاتصال بخدمة الذكاء الاصطناعي. حاول مرة أخرى." }

    suspend fun generateImageForScene(prompt: String): String {
        throw IllegalStateException("Image generation provider is not configured")
    }

    data class ProjectBlueprintResult(
        val title: String,
        val genre: String,
        val duration: Int,
        val heroName: String,
        val heroRole: String,
        val heroDescription: String,
        val scenesCount: Int
    )

    suspend fun generateProjectBlueprint(promptText: String): ProjectBlueprintResult? = runCatching {
        val prompt = """
            You are the AI Director for Night Tales Studio.
            Create a high-level story blueprint for this idea:
            $promptText

            Return ONLY valid JSON with exactly these fields:
            {
              "title": "Arabic title",
              "genre": "Arabic genre",
              "duration": 5,
              "heroName": "main character name",
              "heroRole": "main character role",
              "heroDescription": "visual description in Arabic",
              "scenesCount": 8
            }
        """.trimIndent()

        val raw = generate(
            GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(
                    responseMimeType = "application/json",
                    temperature = 0.2f
                )
            )
        ).trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()

        val json = JSONObject(raw)
        ProjectBlueprintResult(
            title = json.optString("title", "قصة جديدة").trim().ifBlank { "قصة جديدة" },
            genre = json.optString("genre", "خيال").trim().ifBlank { "خيال" },
            duration = json.optInt("duration", 5).coerceIn(1, 180),
            heroName = json.optString("heroName", "البطل").trim().ifBlank { "البطل" },
            heroRole = json.optString("heroRole", "شخصية رئيسية").trim().ifBlank { "شخصية رئيسية" },
            heroDescription = json.optString("heroDescription", "وصف البطل").trim(),
            scenesCount = json.optInt("scenesCount", 6).coerceIn(1, 100)
        )
    }.getOrNull()
}
