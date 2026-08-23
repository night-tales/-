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
                textResponse.contains("CREATE_VIDEO") -> AiAction.CreateVideo("temp_id")
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
            "عذراً، حدث خطأ في التواصل مع الاستوديو. حاول مرة أخرى."
        }
    }

    suspend fun generateImageForScene(prompt: String): String = withContext(Dispatchers.IO) {
        // Since Gemini API does not natively support image generation, 
        // we use a free AI image generation endpoint (Pollinations AI)
        // using the scene description/prompt for the image text.
        val encodedPrompt = java.net.URLEncoder.encode(prompt, "UTF-8")
        "https://image.pollinations.ai/prompt/$encodedPrompt?width=800&height=400&nologo=true"
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

    data class ScenePlan(
        val title: String,
        val narration: String,
        val imagePrompt: String,
        val durationMs: Long
    )

    suspend fun generateScenes(projectTitle: String, count: Int): List<ScenePlan> = withContext(Dispatchers.IO) {
        val prompt = """
            You are the AI Director for Night Tales Studio.
            The user is creating a story titled: "$projectTitle".
            Please generate $count sequential scenes for this story.
            Return a valid JSON array matching exactly this structure, with no markdown formatting or extra text:
            [
              {
                "title": "Scene 1: Introduction",
                "narration": "Narration text in Arabic",
                "imagePrompt": "Detailed visual description of the scene in English for AI image generator",
                "durationMs": 5000
              }
            ]
        """.trimIndent()
        try {
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(responseMimeType = "application/json")
            )
            val response = geminiApi.generateContent(BuildConfig.GEMINI_API_KEY, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
            
            val plans = mutableListOf<ScenePlan>()
            if (jsonText != null) {
                val array = org.json.JSONArray(jsonText)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    plans.add(
                        ScenePlan(
                            title = obj.optString("title", "مشهد جديد"),
                            narration = obj.optString("narration", "نص سردي..."),
                            imagePrompt = obj.optString("imagePrompt", "Cinematic shot of..."),
                            durationMs = obj.optLong("durationMs", 5000L)
                        )
                    )
                }
            }
            // Pad if less
            while (plans.size < count) {
                plans.add(ScenePlan("مشهد جديد", "نص السرد", "Prompt", 5000L))
            }
            plans.take(count)
        } catch (e: Exception) {
            e.printStackTrace()
            List(count) { ScenePlan("مشهد جديد", "نص السرد", "Prompt", 5000L) }
        }
    }

    
    data class CharacterBlueprintResult(
        val name: String,
        val description: String,
        val visualPrompt: String,
        val traits: String
    )

    suspend fun generateCharacterBlueprint(promptText: String): CharacterBlueprintResult? = withContext(Dispatchers.IO) {
        val prompt = """
            You are the AI Character Designer for Night Tales Studio.
            The user wants to create a character based on the following idea: "$promptText"
            Create a detailed character blueprint.
            Return a valid JSON object matching exactly this structure, with no markdown formatting or extra text:
            {
              "name": "Character name in Arabic",
              "description": "Short description of personality and background in Arabic",
              "visualPrompt": "Detailed visual description of the character in English for AI image generator (clothing, face, hair, colors). Very descriptive.",
              "traits": "Age, gender, main trait, etc. in Arabic (e.g. '12 عاماً، شجاع، يرتدي قبعة حمراء')"
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
                CharacterBlueprintResult(
                    name = json.optString("name", "شخصية مجهولة"),
                    description = json.optString("description", "بدون وصف"),
                    visualPrompt = json.optString("visualPrompt", "A character"),
                    traits = json.optString("traits", "عادي")
                )
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun generateStoryText(promptText: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            أنت كاتب قصص مبدع ومحترف في استوديو "حكايات الليل".
            اكتب قصة قصيرة مشوقة باللغة العربية الفصحى بناءً على الفكرة التالية:
            "$promptText"
            يجب أن تكون القصة منسقة بشكل جميل، مقسمة إلى فقرات، وخالية من أي رموز أو نصوص برمجية (Markdown)، ومناسبة للقراءة مباشرة.
        """.trimIndent()
        
        try {
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt))))
            )
            val response = geminiApi.generateContent(BuildConfig.GEMINI_API_KEY, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                ?: "عذراً، لم أتمكن من توليد القصة. حاول مجدداً."
        } catch (e: Exception) {
            e.printStackTrace()
            "حدث خطأ أثناء توليد القصة: ${e.message}"
        }
    }
}
