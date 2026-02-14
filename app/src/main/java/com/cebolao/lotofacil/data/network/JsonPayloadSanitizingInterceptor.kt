package com.cebolao.lotofacil.data.network

import com.cebolao.lotofacil.core.utils.AppLogger
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.zip.GZIPInputStream

/**
 * Normalizes lottery API payloads before Retrofit conversion.
 *
 * Handles:
 * - gzipped payloads without correct Content-Encoding
 * - inconsistent Content-Type headers for JSON responses
 * - diagnostic logging of status/headers/body sizes
 */
class JsonPayloadSanitizingInterceptor(
    private val logger: AppLogger
) : Interceptor {

    companion object {
        private const val TAG = "JsonPayloadSanitizer"
        private val JSON_MEDIA_TYPE: MediaType = "application/json; charset=utf-8".toMediaType()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val responseBody = response.body ?: return response

        if (request.method.equals("HEAD", ignoreCase = true)) {
            return response
        }

        val rawBytes = runCatching { responseBody.bytes() }
            .getOrElse { error ->
                logger.w(TAG, "Failed to read response body for ${request.url}", error)
                return response
            }

        val contentEncodingHeader = response.header("Content-Encoding").orEmpty()
        val hasGzipHeader = contentEncodingHeader.contains("gzip", ignoreCase = true)
        val hasGzipSignature = rawBytes.isGzipPayload()

        val decodedBytes = when {
            hasGzipHeader || hasGzipSignature -> {
                runCatching { rawBytes.gunzip() }
                    .onFailure { error ->
                        logger.w(
                            TAG,
                            "Failed to gunzip payload for ${request.url}. Falling back to raw bytes.",
                            error
                        )
                    }
                    .getOrDefault(rawBytes)
            }
            else -> rawBytes
        }

        val normalizedMediaType = resolveMediaType(
            currentType = responseBody.contentType(),
            contentTypeHeader = response.header("Content-Type"),
            bytes = decodedBytes
        )

        logger.d(
            TAG,
            "HTTP ${response.code} ${request.method} ${request.url} | " +
                "Content-Type=${response.header("Content-Type")} | " +
                "Content-Encoding=${response.header("Content-Encoding")} | " +
                "Content-Length=${response.header("Content-Length")} | " +
                "rawBytes=${rawBytes.size} decodedBytes=${decodedBytes.size} | " +
                "gzipHeader=$hasGzipHeader gzipSignature=$hasGzipSignature"
        )

        if (logger.isDebug) {
            logger.d(TAG, "Headers for ${request.url}: ${response.headers}")
        }

        val rebuiltResponse = response.newBuilder()
            .removeHeader("Content-Length")
            .header("Content-Type", normalizedMediaType.toString())
            .body(decodedBytes.toResponseBody(normalizedMediaType))

        if (hasGzipHeader || hasGzipSignature) {
            rebuiltResponse.removeHeader("Content-Encoding")
        }

        return rebuiltResponse.build()
    }

    private fun resolveMediaType(
        currentType: MediaType?,
        contentTypeHeader: String?,
        bytes: ByteArray
    ): MediaType {
        val headerType = contentTypeHeader
            ?.substringBefore(';')
            ?.trim()
            ?.lowercase()

        if (headerType?.contains("json") == true || currentType?.subtype?.contains("json") == true) {
            return currentType ?: JSON_MEDIA_TYPE
        }

        return if (bytes.looksLikeJson()) JSON_MEDIA_TYPE else (currentType ?: JSON_MEDIA_TYPE)
    }

    private fun ByteArray.isGzipPayload(): Boolean {
        return size >= 2 && this[0] == 0x1F.toByte() && this[1] == 0x8B.toByte()
    }

    private fun ByteArray.gunzip(): ByteArray {
        return GZIPInputStream(inputStream()).use { gzipStream -> gzipStream.readBytes() }
    }

    private fun ByteArray.looksLikeJson(): Boolean {
        val firstNonWhitespace = firstOrNull { byte ->
            byte != ' '.code.toByte() &&
                byte != '\n'.code.toByte() &&
                byte != '\r'.code.toByte() &&
                byte != '\t'.code.toByte()
        } ?: return false

        return when (firstNonWhitespace.toInt().toChar()) {
            '{', '[', '"', 't', 'f', 'n', '-' -> true
            else -> firstNonWhitespace in '0'.code.toByte()..'9'.code.toByte()
        }
    }
}
