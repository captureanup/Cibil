package com.example.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class GeminiProvider {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val defaultModel = "gemini-2.5-flash"

    fun getApiKey(userCustomKey: String?): String {
        if (!userCustomKey.isNullOrBlank()) return userCustomKey.trim()
        return try {
            val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
            val key = field.get(null) as? String ?: ""
            key.trim()
        } catch (_: Exception) {
            ""
        }
    }

    fun isConfigured(userCustomKey: String?): Boolean {
        val key = getApiKey(userCustomKey)
        return key.isNotBlank() && !key.equals("MY_GEMINI_API_KEY", ignoreCase = true)
    }

    fun streamChatResponse(
        prompt: String,
        systemInstruction: String,
        userCustomKey: String?,
        tools: List<ToolDefinition>
    ): Flow<AiStreamEvent> = flow {
        val apiKey = getApiKey(userCustomKey)

        if (apiKey.isBlank() || apiKey.equals("MY_GEMINI_API_KEY", ignoreCase = true)) {
            emit(AiStreamEvent.Started("gemini-2.5-flash"))
            emit(
                AiStreamEvent.Failed(
                    "AI response service is not configured yet. Please configure your GEMINI_API_KEY in Settings or AI Studio Secrets to connect the Gemini Brain."
                )
            )
            return@flow
        }

        emit(AiStreamEvent.Started(defaultModel))

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$defaultModel:streamGenerateContent?alt=sse&key=$apiKey"

        val requestJson = JSONObject().apply {
            // System instruction
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", systemInstruction))
                })
            })

            // Content
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    })
                })
            })

            // Generation config
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("maxOutputTokens", 2048)
            })
        }

        val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val fullTextBuilder = StringBuilder()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errorBody = response.body?.string().orEmpty()
                val message = parseErrorMessage(errorBody, response.code)
                emit(AiStreamEvent.Failed("Gemini API Error (${response.code}): $message"))
                return@flow
            }

            val body = response.body ?: throw IllegalStateException("Empty response body from Gemini API")
            val reader = BufferedReader(InputStreamReader(body.byteStream()))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val currentLine = line ?: continue
                if (currentLine.startsWith("data: ")) {
                    val dataContent = currentLine.removePrefix("data: ").trim()
                    if (dataContent.isNotBlank()) {
                        try {
                            val chunkJson = JSONObject(dataContent)
                            val candidates = chunkJson.optJSONArray("candidates")
                            if (candidates != null && candidates.length() > 0) {
                                val firstCandidate = candidates.getJSONObject(0)
                                val content = firstCandidate.optJSONObject("content")
                                val parts = content?.optJSONArray("parts")
                                if (parts != null) {
                                    for (i in 0 until parts.length()) {
                                        val part = parts.getJSONObject(i)
                                        val text = part.optString("text", "")
                                        if (text.isNotEmpty()) {
                                            fullTextBuilder.append(text)
                                            emit(AiStreamEvent.ContentDelta(text))
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.w("GeminiProvider", "Failed to parse SSE chunk", e)
                        }
                    }
                }
            }

            val resultText = fullTextBuilder.toString()
            if (resultText.isBlank()) {
                emit(AiStreamEvent.Failed("Model returned an empty response."))
            } else {
                emit(AiStreamEvent.Completed(resultText))
            }
        } catch (e: Exception) {
            Log.e("GeminiProvider", "Stream error", e)
            emit(AiStreamEvent.Failed(e.localizedMessage ?: "Failed to communicate with Gemini API"))
        }
    }.flowOn(Dispatchers.IO)

    private fun parseErrorMessage(errorBody: String, code: Int): String {
        return try {
            val json = JSONObject(errorBody)
            val error = json.optJSONObject("error")
            val message = error?.optString("message")
            if (!message.isNullOrBlank()) message else "HTTP $code"
        } catch (_: Exception) {
            "HTTP $code: $errorBody"
        }
    }
}
