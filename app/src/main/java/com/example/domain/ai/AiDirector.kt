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
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class AiDirector @Inject constructor(private val geminiApi: GeminiApiService) {
    private suspend fun generate(request: GenerateContentRequest): String = withContext(Dispatchers.IO) {
        val response = geminiApi.generateContent(BuildConfig.GEMINI_API_KEY, request)
        if (!response.isSuccessful) throw IllegalStateException("Gemini request failed (${response.code()}): ${response.errorBody()?.string()?.take(500).orEmpty()}")
        response.body()?.extractText()?.takeIf { it.isNotBlank() } ?: throw IllegalStateException("Gemini returned an empty response")
    }

    private fun GenerateContentResponse.extractText(): String = candidates?.asSequence()
        ?.mapNotNull { it.content?.parts?.asSequence()?.mapNotNull { part -> part.text.trim() }?.firstOrNull() }
        ?.firstOrNull { it.isNotBlank() } ?: ""

    suspend fun understandIntent(userMessage: String): AiAction = runCatching {
        val prompt = "Analyze the message and return ONLY one token: CREATE_STORY, MODIFY_STORY, CREATE_IMAGE, CREATE_AUDIO, CREATE_VIDEO, UNKNOWN. Message: $userMessage"
        when {
            generate(GenerateContentRequest(contents = listOf(Content(parts = listOf(Part(text = prompt)))), generationConfig = GenerationConfig(temperature = 0f))).uppercase().contains("CREATE_STORY") -> AiAction.CreateStory(userMessage)
            else -> AiAction.Unknown
        }
    }.getOrElse { AiAction.Unknown }

    suspend fun generateChatReply(userMessage: String): String = runCatching {
        generate(GenerateContentRequest(contents = listOf(Content(parts = listOf(Part(text = "أنت المخرج الذكي في Night Tales Studio. أجب بالعربية باختصار وتشجيع. رسالة المستخدم: $userMessage")))))
    }.getOrElse { "عذراً، تعذر الاتصال بخدمة الذكاء الاصطناعي. حاول مرة أخرى." }

    data class ScenePlan(val index: Int, val title: String, val narration: String, val imagePrompt: String, val durationMs: Long)

    suspend fun generateScenes(idea: String, sceneCount: Int): List<ScenePlan> = withContext(Dispatchers.IO) {
        val count = sceneCount.coerceIn(1, 30)
        val prompt = """
            Create exactly $count cinematic scenes for this Night Tales story idea: $idea
            Return ONLY a JSON array. Each item: {"index":0,"title":"...","narration":"...","imagePrompt":"...","durationMs":15000}
            index starts at 0. durationMs is 5000..60000. Write creative text in Arabic.
        """.trimIndent()
        val raw = generate(GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(responseMimeType = "application/json", temperature = 0.3f)
        )).trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        val array = JSONArray(raw)
        if (array.length() != count) throw IllegalStateException("Gemini returned ${array.length()} scenes; expected $count")
        (0 until array.length()).map { i ->
            val json = array.getJSONObject(i)
            ScenePlan(i, json.getString("title").trim(), json.getString("narration").trim(), json.getString("imagePrompt").trim(), json.optLong("durationMs", 15000L).coerceIn(5000L, 60000L))
        }
    }

    suspend fun generateImageForScene(prompt: String): String = throw IllegalStateException("Image generation provider is not configured")

    data class ProjectBlueprintResult(val title: String, val genre: String, val duration: Int, val heroName: String, val heroRole: String, val heroDescription: String, val scenesCount: Int)

    suspend fun generateProjectBlueprint(promptText: String): ProjectBlueprintResult? = runCatching {
        val prompt = "Create a story blueprint for: $promptText. Return ONLY JSON: {\"title\":\"Arabic\",\"genre\":\"Arabic\",\"duration\":5,\"heroName\":\"name\",\"heroRole\":\"role\",\"heroDescription\":\"description\",\"scenesCount\":8}"
        val raw = generate(GenerateContentRequest(contents = listOf(Content(parts = listOf(Part(text = prompt)))), generationConfig = GenerationConfig(responseMimeType = "application/json", temperature = 0.2f))).trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        val json = JSONObject(raw)
        ProjectBlueprintResult(json.getString("title"), json.getString("genre"), json.optInt("duration", 5).coerceIn(1, 180), json.getString("heroName"), json.getString("heroRole"), json.optString("heroDescription"), json.optInt("scenesCount", 6).coerceIn(1, 30))
    }.getOrNull()
}
