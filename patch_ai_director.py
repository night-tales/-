import re

with open('app/src/main/java/com/example/domain/ai/AiDirector.kt', 'r') as f:
    content = f.read()

new_func = """
    data class CharacterBlueprintResult(
        val name: String,
        val description: String,
        val visualPrompt: String,
        val traits: String
    )

    suspend fun generateCharacterBlueprint(promptText: String): CharacterBlueprintResult? = withContext(Dispatchers.IO) {
        val prompt = \"\"\"
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
        \"\"\".trimIndent()
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
"""

content = content.replace(
    'suspend fun generateStoryText(promptText: String): String = withContext(Dispatchers.IO) {',
    new_func + '\n    suspend fun generateStoryText(promptText: String): String = withContext(Dispatchers.IO) {'
)

with open('app/src/main/java/com/example/domain/ai/AiDirector.kt', 'w') as f:
    f.write(content)
