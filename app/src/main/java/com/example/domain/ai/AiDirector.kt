package com.example.domain.ai

import com.example.BuildConfig
import com.example.data.remote.gemini.Content
import com.example.data.remote.gemini.GenerateContentRequest
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
    suspend fun understandIntent(userMessage: String): AiAction = withContext(Dispatchers.IO) {
        val prompt = """
            You are the AI Director for Night Tales Studio.
            Analyze the user's message and determine their intent.
            Return ONLY a raw string representing the intent from this exact list:
            CREATE_STORY
            MODIFY_STORY
            CREATE_IMAGE
            CREATE_AUDIO
            CREATE_VIDEO
            UNKNOWN
            
            User message: "$userMessage"
        """.trimIndent()
        
        try {
            val request = GenerateContentRequest(
                contents = listOf(
                    Content(parts = listOf(Part(text = prompt)))
                )
            )
            val response = geminiApi.generateContent(BuildConfig.GEMINI_API_KEY, request)
            val textResponse = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: "UNKNOWN"
            
            when {
                textResponse.contains("CREATE_STORY") -> AiAction.CreateStory(userMessage)
                textResponse.contains("MODIFY_STORY") -> AiAction.ModifyStory(userMessage)
                textResponse.contains("CREATE_IMAGE") -> AiAction.CreateImage(userMessage)
                textResponse.contains("CREATE_AUDIO") -> AiAction.CreateAudio(userMessage)
                textResponse.contains("CREATE_VIDEO") -> AiAction.CreateVideo("temp_id") // ID would come from context later
                else -> AiAction.Unknown
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AiAction.Unknown
        }
    }

    suspend fun generateChatReply(userMessage: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            أنت المخرج الذكي في استوديو 'حكايات الليل' (Night Tales Studio).
            مهمتك هي مساعدة المستخدم في تحويل أفكاره إلى أفلام قصصية.
            الرد يجب أن يكون قصيراً، مشجعاً، وباللغة العربية الفصحى المبسطة.
            رد على رسالة المستخدم التالية: "$userMessage"
        """.trimIndent()
        
        try {
            val request = GenerateContentRequest(
                contents = listOf(
                    Content(parts = listOf(Part(text = prompt)))
                )
            )
            val response = geminiApi.generateContent(BuildConfig.GEMINI_API_KEY, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() 
                ?: "عذراً، حدث خطأ في التواصل مع الاستوديو. حاول مرة أخرى."
        } catch (e: Exception) {
            e.printStackTrace()
            "يبدو أن هناك مشكلة في الاتصال. لا أستطيع الرد حالياً."
        }
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

    suspend fun generateProjectBlueprint(promptText: String): ProjectBlueprintResult? = withContext(Dispatchers.IO) {
        val prompt = """
            You are the AI Director for Night Tales Studio.
            The user wants to create a story based on the following idea: "$promptText"
            
            Create a high-level project blueprint.
            Return a valid JSON object matching exactly this structure, with no markdown formatting or extra text:
            {
              "title": "A catchy title in Arabic",
              "genre": "Story genre (e.g., مغامرة • خيال)",
              "duration": 5,
              "heroName": "Main character name",
              "heroRole": "Role (e.g., البطل)",
              "heroDescription": "Visual description of the hero in Arabic",
              "scenesCount": 8
            }
        """.trimIndent()

        try {
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(responseMimeType = "application/json")
            )
            val response = geminiApi.generateContent(BuildConfig.GEMINI_API_KEY, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
            
            if (jsonText != null) {
                val json = JSONObject(jsonText)
                ProjectBlueprintResult(
                    title = json.optString("title", "قصة جديدة"),
                    genre = json.optString("genre", "خيال"),
                    duration = json.optInt("duration", 5),
                    heroName = json.optString("heroName", "البطل"),
                    heroRole = json.optString("heroRole", "شخصية رئيسية"),
                    heroDescription = json.optString("heroDescription", "وصف البطل"),
                    scenesCount = json.optInt("scenesCount", 6)
                )
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
