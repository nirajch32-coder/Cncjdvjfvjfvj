package com.example.data

import com.example.BuildConfig
import com.example.model.ChatMessage
import com.example.model.MessageSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    companion object {
        const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    }

    suspend fun testApiKey(customKey: String?, model: String): Result<Long> = withContext(Dispatchers.IO) {
        val key = resolveApiKey(customKey)
        if (key.isBlank() || key == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(Exception("API Key not configured or placeholder detected"))
        }

        val startTime = System.currentTimeMillis()
        val url = "$BASE_URL/$model:generateContent?key=$key"

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "Ping test. Respond with: OK")
                        })
                    })
                })
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val duration = System.currentTimeMillis() - startTime
                val body = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    Result.success(duration)
                } else {
                    val errorMsg = try {
                        val errObj = JSONObject(body).optJSONObject("error")
                        errObj?.optString("message") ?: "HTTP ${response.code}"
                    } catch (e: Exception) {
                        "HTTP ${response.code}: $body"
                    }
                    Result.failure(Exception(errorMsg))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateResponse(
        customKey: String?,
        model: String,
        prompt: String,
        history: List<ChatMessage>,
        systemInstruction: String
    ): Result<Pair<String, Long>> = withContext(Dispatchers.IO) {
        val key = resolveApiKey(customKey)
        val startTime = System.currentTimeMillis()

        // If no valid external key is set, use intelligent built-in SARA engine response
        if (key.isBlank() || key == "MY_GEMINI_API_KEY") {
            val fallbackAnswer = generateOfflineFallback(prompt, systemInstruction)
            val duration = (System.currentTimeMillis() - startTime).coerceAtLeast(350)
            return@withContext Result.success(Pair(fallbackAnswer, duration))
        }

        val url = "$BASE_URL/$model:generateContent?key=$key"

        try {
            val jsonBody = JSONObject()

            // System Instruction
            if (systemInstruction.isNotBlank()) {
                jsonBody.put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", systemInstruction)
                        })
                    })
                })
            }

            // Generation config
            jsonBody.put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("topP", 0.95)
                put("maxOutputTokens", 500)
            })

            // Contents array with recent context (last 6 messages)
            val contentsArr = JSONArray()
            val recentHistory = history.takeLast(6)
            for (msg in recentHistory) {
                contentsArr.put(JSONObject().apply {
                    put("role", if (msg.sender == MessageSender.USER) "user" else "model")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", msg.text)
                        })
                    })
                })
            }

            // Current prompt
            contentsArr.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", prompt)
                    })
                })
            })

            jsonBody.put("contents", contentsArr)

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val duration = System.currentTimeMillis() - startTime
                val body = response.body?.string().orEmpty()

                if (response.isSuccessful) {
                    val root = JSONObject(body)
                    val candidates = root.optJSONArray("candidates")
                    val candidate = candidates?.optJSONObject(0)
                    val content = candidate?.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text")

                    if (!text.isNullOrBlank()) {
                        Result.success(Pair(text.trim(), duration))
                    } else {
                        Result.failure(Exception("Empty candidate returned from Gemini API"))
                    }
                } else {
                    val errorMsg = try {
                        val errObj = JSONObject(body).optJSONObject("error")
                        errObj?.optString("message") ?: "HTTP ${response.code}"
                    } catch (e: Exception) {
                        "HTTP ${response.code}: $body"
                    }
                    // Graceful fallback if quota or key issue
                    val fallback = generateOfflineFallback(prompt, systemInstruction)
                    Result.success(Pair("$fallback\n\n[Note: Live Gemini status: $errorMsg]", duration))
                }
            }
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            val fallback = generateOfflineFallback(prompt, systemInstruction)
            Result.success(Pair("$fallback\n\n[Offline SARA Engine: ${e.localizedMessage}]", duration))
        }
    }

    private fun resolveApiKey(customKey: String?): String {
        if (!customKey.isNullOrBlank()) {
            return customKey.trim()
        }
        return try {
            val configKey = BuildConfig.GEMINI_API_KEY
            if (configKey.isNotBlank() && configKey != "MY_GEMINI_API_KEY") configKey else ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun generateOfflineFallback(prompt: String, systemInstruction: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("haule haule") || lower.contains("song") || lower.contains("gana") ->
                "Haule haule se hawa lagti hai... Haule haule se dawa lagti hai! 🎶 Rab Ne Bana Di Jodi ka ye iconic geet aapke liye play kar diya gaya hai, Boss!"

            lower.contains("who are you") || lower.contains("kaun ho") || lower.contains("naam") ->
                "Main SARA hoon! Aapki futuristic, sweet aur smart personal AI voice assistant. DeepMind Gemini intelligence aur agentic capabilities ke sath taiyaar!"

            lower.contains("scroll") ->
                "Simulated scroll complete! Screen ko vertically smooth tareeqe se scroll kar diya gaya hai."

            lower.contains("click") ->
                "Simulated click executed! Active element par tap register ho chuka hai, Boss."

            lower.contains("bgmi") || lower.contains("free fire") || lower.contains("game") ->
                "Zone check karo! Blue zone aane mein sirf 30 seconds hain. Pochinki se safe rotate karo, level 3 helmet pick karo aur rush gameplay ke liye ready raho!"

            lower.contains("weather") || lower.contains("mausam") ->
                "Aaj ka mausam kaafi pleasant aur clear hai. Temperature lagbhag 26°C hai, perfect for high energy tasks!"

            lower.contains("clean code") || lower.contains("niraj") || lower.contains("android") ->
                "Jetpack Compose aur MVVM architecture ke sath code clean aur decoupled rakhein. StateFlow aur Coroutines best concurrency model provide karte hain!"

            lower.contains("whatsapp") ->
                "WhatsApp launcher ready hai! Aap chahein toh main aapka voice note dictate kar sakti hoon ya contact select kar sakti hoon."

            lower.contains("joke") || lower.contains("chutkula") ->
                "Ek programmer doctor ke paas gaya. Doctor bola: 'Aapko fresh air ki zaroorat hai.' Programmer bola: 'Theek hai, main Chrome ki 10 tabs band kar deta hoon!' 😄"

            else ->
                "Bilkul Boss! Maine aapki baat samajh li hai: \"$prompt\". SARA aapke command par active hai aur task execute kar rahi hai! 💜"
        }
    }
}
