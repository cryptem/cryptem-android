package io.cryptem.app.model.binance

import okhttp3.internal.and
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HmacUtil {
    fun hmac256(message: String, key: String) : String? {
        try {
            val hashingAlgorithm = "HmacSHA256"
            val bytes = hmac(hashingAlgorithm, key.toByteArray(), message.toByteArray())
            return bytesToHex(bytes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun hmac(algorithm: String?, key: ByteArray?, message: ByteArray?): ByteArray {
        val mac = Mac.getInstance(algorithm)
        mac.init(SecretKeySpec(key, algorithm))
        return mac.doFinal(message)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexArray = "0123456789abcdef".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
        var j = 0
        var v: Int
        while (j < bytes.size) {
            v = bytes[j] and 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
            j++
        }
        return String(hexChars)
    }
}