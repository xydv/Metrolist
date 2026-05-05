package com.metrolist.music.utils

import com.metrolist.music.models.MediaMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import org.json.JSONObject

class WebhookManager(
    private val scope: CoroutineScope,
    private val httpClient: OkHttpClient = OkHttpClient()
) {
    var webhookUrl: String? = null

    fun onSongChanged(metadata: MediaMetadata?) {
        val url = webhookUrl ?: return
        if (metadata == null) return

        scope.launch(Dispatchers.IO) {
            try {
                val json = JSONObject().apply {
                    put("id", metadata.id)
                    put("title", metadata.title)
                    put("artist", metadata.artists.joinToString { it.name })
                    put("album", metadata.album?.title)
                    put("duration", metadata.duration)
                    put("thumbnailUrl", metadata.thumbnailUrl)
                    put("liked", metadata.liked)
                }

                val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Timber.e("Webhook failed with code: ${response.code}")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error sending webhook request")
            }
        }
    }
}