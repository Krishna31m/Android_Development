package com.aicareermentor.data.remote.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val initialDelayMillis: Long = 2000,
    private val factor: Double = 2.0
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        var tryCount = 0

        while (response.code == 429 && tryCount < maxRetries) {
            tryCount++

            // When API returns 0s for retry, we use our own exponential backoff
            val delayMillis = getRetryDelay(response) ?: (initialDelayMillis * factor.pow(tryCount - 1)).toLong()

            response.close() // Close the previous response to avoid resource leaks

            Log.d("RetryInterceptor", "Rate limit hit. Retrying in ${delayMillis}ms. Attempt #$tryCount")

            try {
                Thread.sleep(delayMillis)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                throw IOException("Retry interrupted", e)
            }

            // Retry the request
            response = chain.proceed(request)
        }

        return response
    }

    private fun getRetryDelay(response: Response): Long? {
        return try {
            // Peek the body so it can be read again later by Retrofit
            val responseBody = response.peekBody(Long.MAX_VALUE).string()
            val json = JSONObject(responseBody)
            val error = json.optJSONObject("error")
            val details = error?.optJSONArray("details")

            if (details != null) {
                for (i in 0 until details.length()) {
                    val detail = details.getJSONObject(i)
                    if (detail.optString("@type") == "type.googleapis.com/google.rpc.RetryInfo") {
                        val retryDelayStr = detail.optString("retryDelay")
                        return parseRetryDelay(retryDelayStr)
                    }
                }
            }
            null
        } catch (e: Exception) { // JSONException, IOException, etc.
            Log.e("RetryInterceptor", "Failed to parse retry delay from response", e)
            null
        }
    }

    private fun parseRetryDelay(retryDelay: String?): Long? {
        if (retryDelay.isNullOrEmpty() || !retryDelay.endsWith('s')) {
            return null
        }
        return try {
            val seconds = retryDelay.removeSuffix("s").toLong()
            if (seconds > 0) {
                TimeUnit.SECONDS.toMillis(seconds)
            } else {
                null // Fallback to exponential backoff if delay is 0 or less
            }
        } catch (e: NumberFormatException) {
            Log.e("RetryInterceptor", "Failed to parse retry delay value: $retryDelay", e)
            null
        }
    }
}
